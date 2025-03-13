package com.news.sports.controller;

import com.news.sports.common.Result;
import com.news.sports.entity.Cart;
import com.news.sports.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public Result<Cart> addToCart(@RequestBody Cart cart) {
        return Result.success(cartService.addToCart(cart));
    }

    @GetMapping("/list")
    public Result<List<Cart>> getCartList() {
        return Result.success(cartService.getCartList());
    }

    @PutMapping
    public Result<Void> updateCartQuantity(@RequestBody Cart cart) {
        cartService.updateCartQuantity(cart);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart() {
        cartService.clearCart();
        return Result.success();
    }
} 