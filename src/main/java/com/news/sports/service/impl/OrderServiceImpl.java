package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Order;
import com.news.sports.entity.Product;
import com.news.sports.entity.User;
import com.news.sports.mapper.OrderMapper;
import com.news.sports.service.OrderService;
import com.news.sports.service.ProductService;
import com.news.sports.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final UserService userService;

    public OrderServiceImpl(OrderMapper orderMapper, ProductService productService, UserService userService) {
        this.orderMapper = orderMapper;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        // 检查商品库存
        Product product = productService.getProductById(order.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        // 生成订单号
        order.setOrderNo(generateOrderNo());
        order.setStatus(0); // 待支付状态
        orderMapper.insert(order);

        // 扣减库存
        product.setStock(product.getStock() - order.getQuantity());
        productService.updateProduct(product);

        return order;
    }

    @Override
    @Transactional
    public void updateOrder(Order order) {
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderMapper.deleteById(id);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
    }

    @Override
    public Page<Order> getUserOrders(Long userId, Integer page, Integer size) {
        return orderMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime));
    }

    @Override
    public Page<Order> getAllOrders(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Order::getOrderNo, keyword)
                    .or()
                    .like(Order::getReceiver, keyword)
                    .or()
                    .like(Order::getPhone, keyword);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void payOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }

        // 如果是积分支付，扣减用户积分
        if (order.getPayType() == 1) {
            Product product = productService.getProductById(order.getProductId());
            int totalPoints = product.getPoints() * order.getQuantity();
            User user = userService.getCurrentUser();
            if (user.getPoints() < totalPoints) {
                throw new RuntimeException("积分不足");
            }
            userService.updatePoints(user.getId(), -totalPoints);
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 增加商品销量
        productService.incrementSalesCount(order.getProductId());
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus(4);
        orderMapper.updateById(order);

        // 恢复商品库存
        Product product = productService.getProductById(order.getProductId());
        product.setStock(product.getStock() + order.getQuantity());
        productService.updateProduct(product);
    }

    @Override
    @Transactional
    public void deliverOrder(String orderNo, String expressNo, String expressCompany) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus(2);
        order.setExpressNo(expressNo);
        order.setExpressCompany(expressCompany);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus(3);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void refundOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        // 如果是积分支付，退还用户积分
        if (order.getPayType() == 1) {
            Product product = productService.getProductById(order.getProductId());
            int totalPoints = product.getPoints() * order.getQuantity();
            userService.updatePoints(order.getUserId(), totalPoints);
        }

        order.setStatus(4);
        orderMapper.updateById(order);

        // 恢复商品库存
        Product product = productService.getProductById(order.getProductId());
        product.setStock(product.getStock() + order.getQuantity());
        productService.updateProduct(product);
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
} 