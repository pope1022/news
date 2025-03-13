package com.news.sports.service;

import com.news.sports.entity.Cart;
import java.util.List;

public interface CartService {
    Cart addToCart(Cart cart);
    
    List<Cart> getCartList();
    
    void updateCartQuantity(Cart cart);
    
    void removeFromCart(Long id);
    
    void clearCart();
} 