package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Order;

public interface OrderService {
    Order createOrder(Order order);
    
    void updateOrder(Order order);
    
    void deleteOrder(Long id);
    
    Order getOrderById(Long id);
    
    Order getOrderByOrderNo(String orderNo);
    
    Page<Order> getUserOrders(Long userId, Integer page, Integer size);
    
    Page<Order> getAllOrders(Integer page, Integer size, String keyword);
    
    void payOrder(String orderNo);
    
    void cancelOrder(String orderNo);
    
    void deliverOrder(String orderNo, String expressNo, String expressCompany);
    
    void completeOrder(String orderNo);
    
    void refundOrder(String orderNo);
} 