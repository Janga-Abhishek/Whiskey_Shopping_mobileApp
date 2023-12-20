package com.example.abhishek_janga_project2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek_janga_project2.models.CartItem;
import com.example.abhishek_janga_project2.models.Product;
import com.example.abhishek_janga_project2.Cart;
import com.example.abhishek_janga_project2.Shop;

import java.util.List;

public class ShopViewModel extends ViewModel {

    private final Shop shop = new Shop();
    private final Cart cart = new Cart();

    private final MutableLiveData<Product> mutableProduct = new MutableLiveData<>();
    private final LiveData<List<Product>> products = shop.getProducts();

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void setProduct(Product product) {
        mutableProduct.setValue(product);
    }

    public LiveData<Product> getProduct() {
        return mutableProduct;
    }

    public LiveData<List<CartItem>> getCart() {
        return cart.getCart();
    }

    public boolean addItemToCart(Product product) {
        return cart.addItemToCart(product);
    }

    public void removeItemFromCart(CartItem cartItem) {
        cart.removeItemFromCart(cartItem.getId());
    }

    public void changeQuantity(CartItem cartItem, int quantity) {
        cart.changeQuantity(cartItem.getId(), quantity);

    }

    public LiveData<Double> getTotalPrice() {
        return cart.getTotalPrice();
    }

    public void resetCart() {
        cart.initCart();
    }

    public void resetCartAfterCheckout() {
        cart.resetCartCheckout();
    }
}
