package com.example.abhishek_janga_project2;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.abhishek_janga_project2.models.CartItem;
import com.example.abhishek_janga_project2.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    private FirebaseFirestore firestoreDB;
    private CollectionReference cartCollection;
    private MutableLiveData<List<CartItem>> mutableCart = new MutableLiveData<>();
    private MutableLiveData<Double> mutableTotalPrice = new MutableLiveData<>();

    public Cart() {
        firestoreDB = FirebaseFirestore.getInstance();
        String currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartCollection = firestoreDB.collection("user_cart").document(currentUserKey).collection("cart");
        initCart(); // Initialize cart data from Firestore
    }

    public LiveData<List<CartItem>> getCart() {
        return mutableCart;
    }

    public void initCart() {
        cartCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CartItem> cartItemList = new ArrayList<>();
                for (DocumentSnapshot snapshot : task.getResult()) {
                    // Parse CartItem objects from Firestore data and add to the cartItemList
                    CartItem cartItem = snapshot.toObject(CartItem.class);
                    cartItemList.add(cartItem);
                }
                mutableCart.setValue(cartItemList);
                calculateCartTotal();
            } else {
                // Handle potential errors here
            }
        });
    }

    public boolean addItemToCart(Product product) {
        if (mutableCart.getValue() == null) {
            initCart(); // Ensure cart data is initialized
        }
        List<CartItem> cartItemList = new ArrayList<>(mutableCart.getValue());
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                if (cartItem.getQuantity() == 5) {
                    return false;
                }

                int index = cartItemList.indexOf(cartItem);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemList.set(index, cartItem);

                mutableCart.setValue(cartItemList);
                calculateCartTotal();
                updateCartInFirestore(cartItemList); // Update Firebase here
                return true;
            }
        }
        CartItem cartItem = new CartItem(generateUniqueID(),product, 1);
        cartItemList.add(cartItem);
        mutableCart.setValue(cartItemList);
        calculateCartTotal();
        updateCartInFirestore(cartItemList); // Update Firebase here
        return true;
    }

    public void removeItemFromCart(String cartItemId) {
        cartCollection.document(cartItemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // If successful, update the local cart data after removing from Firestore
                    List<CartItem> updatedCart = new ArrayList<>(mutableCart.getValue());
                    updatedCart.removeIf(cartItem -> cartItem.getId().equals(cartItemId));
                    mutableCart.setValue(updatedCart);
                    calculateCartTotal();
                })
                .addOnFailureListener(e -> {
                    Log.d("error","failed");
                });
    }

    public void changeQuantity(String cartItemId, int quantity) {
        if (mutableCart.getValue() == null) {
            return;
        }
        List<CartItem> cartItemList = new ArrayList<>(mutableCart.getValue());

        // Iterating through the cartItemList
        for (int i = 0; i < cartItemList.size(); i++) {
            CartItem item = cartItemList.get(i);
            if (item.getId().equals(cartItemId)) {
                item.setQuantity(quantity);
                cartItemList.set(i, item);
                break;
            }
        }
        mutableCart.setValue(cartItemList);
        updateCartInFirestore(cartItemList);
        calculateCartTotal();

    }


    private void updateCartInFirestore(List<CartItem> cartItemList) {
        String currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (CartItem cartItem : cartItemList) {
            cartCollection.document(cartItem.getId()).set(cartItem);
        }
    }

    private void calculateCartTotal() {
        if (mutableCart.getValue() == null) return;
        double total = 0.0;
        List<CartItem> cartItemList = mutableCart.getValue();
        for (CartItem cartItem : cartItemList) {
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedTotal = decimalFormat.format(total);
        total = Double.parseDouble(formattedTotal);
        mutableTotalPrice.setValue(total);
    }

    public LiveData<Double> getTotalPrice() {
        return mutableTotalPrice;
    }

    private String generateUniqueID() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
    public void clearLocalCart() {
        mutableCart.setValue(new ArrayList<>());
    }
    public void resetCartCheckout() {
        cartCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CartItem> cartItemList = new ArrayList<>();
                for (DocumentSnapshot snapshot : task.getResult()) {
                    CartItem cartItem = snapshot.toObject(CartItem.class);
                    cartCollection.document(cartItem.getId()).delete();
                }
                mutableCart.setValue(cartItemList);
                calculateCartTotal();
            } else {
            }
        });
    }
}

