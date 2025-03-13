package com.news.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.common.Result;
import com.news.sports.entity.Order;
import com.news.sports.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody Order order) {
        return Result.success(orderService.createOrder(order));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    @GetMapping("/no/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        return Result.success(orderService.getOrderByOrderNo(orderNo));
    }

    @GetMapping("/user")
    public Result<Page<Order>> getUserOrders(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getUserOrders(userId, page, size));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(orderService.getAllOrders(page, size, keyword));
    }

    @PostMapping("/{orderNo}/pay")
    public Result<Void> payOrder(@PathVariable String orderNo) {
        orderService.payOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deliverOrder(
            @PathVariable String orderNo,
            @RequestParam String expressNo,
            @RequestParam String expressCompany) {
        orderService.deliverOrder(orderNo, expressNo, expressCompany);
        return Result.success();
    }

    @PostMapping("/{orderNo}/complete")
    public Result<Void> completeOrder(@PathVariable String orderNo) {
        orderService.completeOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refundOrder(@PathVariable String orderNo) {
        orderService.refundOrder(orderNo);
        return Result.success();
    }
} 