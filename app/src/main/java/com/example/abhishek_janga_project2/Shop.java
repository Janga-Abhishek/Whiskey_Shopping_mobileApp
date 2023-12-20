package com.example.abhishek_janga_project2;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abhishek_janga_project2.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private MutableLiveData<List<Product>> mutableProductList;
    private DatabaseReference databaseReference;

    public LiveData<List<Product>> getProducts() {
        if (mutableProductList == null) {
            mutableProductList = new MutableLiveData<>();
            loadProducts();
        }
        return mutableProductList;
    }

    private void loadProducts() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey(); // Get the unique Firebase key
                    String name = snapshot.child("name").getValue(String.class);
                    String priceString = snapshot.child("cost").getValue(String.class);
                    String description=snapshot.child("description").getValue(String.class);
                    String type=snapshot.child("type").getValue(String.class);


                    double price = 0.0; // Default value if conversion fails
                    if (priceString != null && !priceString.isEmpty()) {
                        try {
                            price = Double.parseDouble(priceString);
                        } catch (NumberFormatException e) {
                            // Handle parsing error
                            e.printStackTrace();
                        }
                    }
                    boolean available = true; // Set availability based on logic
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    Product product = new Product(id, name, price, available, imageUrl, description,type);
                    productList.add(product);
                }
                mutableProductList.setValue(productList);
                Log.d("ShopRepo", "Products loaded: " + mutableProductList.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("ShopRepo", "Failed to read value.", databaseError.toException());
            }
        });

    }
}

