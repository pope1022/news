package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.news.sports.entity.Cart;
import com.news.sports.entity.Product;
import com.news.sports.mapper.CartMapper;
import com.news.sports.service.CartService;
import com.news.sports.service.ProductService;
import com.news.sports.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final UserService userService;
    private final ProductService productService;

    public CartServiceImpl(CartMapper cartMapper, UserService userService, ProductService productService) {
        this.cartMapper = cartMapper;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Cart addToCart(Cart cart) {
        // 设置用户ID
        cart.setUserId(userService.getCurrentUser().getId());
        
        // 检查商品是否存在且有库存
        Product product = productService.getProductById(cart.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() < cart.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 检查购物车是否已存在该商品
        Cart existingCart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getProductId, cart.getProductId()));
        
        if (existingCart != null) {
            // 更新数量
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            cartMapper.updateById(existingCart);
            return existingCart;
        } else {
            // 新增购物车项
            cartMapper.insert(cart);
            return cart;
        }
    }

    @Override
    public List<Cart> getCartList() {
        return cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userService.getCurrentUser().getId())
                .orderByDesc(Cart::getCreateTime));
    }

    @Override
    @Transactional
    public void updateCartQuantity(Cart cart) {
        // 验证购物车项所属
        Cart existingCart = cartMapper.selectById(cart.getId());
        if (existingCart == null || !existingCart.getUserId().equals(userService.getCurrentUser().getId())) {
            throw new RuntimeException("无权限修改此购物车");
        }
        
        // 检查商品库存
        Product product = productService.getProductById(existingCart.getProductId());
        if (product.getStock() < cart.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }
        
        existingCart.setQuantity(cart.getQuantity());
        cartMapper.updateById(existingCart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long id) {
        // 验证购物车项所属
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userService.getCurrentUser().getId())) {
            throw new RuntimeException("无权限删除此购物车");
        }
        
        cartMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void clearCart() {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userService.getCurrentUser().getId()));
    }
} 