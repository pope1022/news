package com.news.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.common.Result;
import com.news.sports.entity.*;
import com.news.sports.service.*;
import com.news.sports.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final UserService userService;
    private final NewsService newsService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public AdminController(
            UserService userService,
            NewsService newsService,
            ProductService productService,
            OrderService orderService,
            CategoryService categoryService,
            CommentService commentService) {
        this.userService = userService;
        this.newsService = newsService;
        this.productService = productService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    // 文件上传
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = FileUtil.upload(file, uploadPath);
            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/" + fileName);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    // 仪表盘数据统计
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取用户统计
        Page<User> users = userService.getUsers(1, 10, null);
        stats.put("userCount", users.getTotal());
        
        // 获取新闻统计
        Page<News> news = newsService.getNewsList(1, 10, null, null);
        stats.put("newsCount", news.getTotal());
        
        // 获取商品统计
        Page<Product> products = productService.getProducts(1, 10, null);
        stats.put("productCount", products.getTotal());
        
        // 获取订单统计
        Page<Order> orders = orderService.getAllOrders(1, 10, null);
        stats.put("orderCount", orders.getTotal());
        
        return Result.success(stats);
    }

    // 获取最新新闻列表
    @GetMapping("/news/latest")
    public Result<Page<News>> getLatestNews() {
        return Result.success(newsService.getLatestNews(1, 5));
    }

    // 获取最新订单列表
    @GetMapping("/orders/latest")
    public Result<Page<Order>> getLatestOrders() {
        return Result.success(orderService.getAllOrders(1, 5, null));
    }

    // 用户管理
    @GetMapping("/users")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getUsers(page, size, keyword));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        if (status == 0) {
            userService.enableUser(id);
        } else {
            userService.disableUser(id);
        }
        return Result.success();
    }

    // 新闻管理
    @GetMapping("/news")
    public Result<Page<News>> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(newsService.getNewsList(page, size, keyword, categoryId));
    }

    @PostMapping("/news")
    public Result<News> createNews(@RequestBody News news) {
        return Result.success(newsService.createNews(news));
    }

    @PutMapping("/news/{id}")
    public Result<Void> updateNews(@RequestBody News news) {
        newsService.updateNews(news);
        return Result.success();
    }

    @DeleteMapping("/news/{id}")
    public Result<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return Result.success();
    }

    @PutMapping("/news/{id}/publish")
    public Result<Void> publishNews(@PathVariable Long id) {
        newsService.publishNews(id);
        return Result.success();
    }

    @PutMapping("/news/{id}/unpublish")
    public Result<Void> unpublishNews(@PathVariable Long id) {
        newsService.unpublishNews(id);
        return Result.success();
    }

    // 分类管理
    @GetMapping("/categories")
    public Result<Page<Category>> getCategoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(categoryService.getCategories(page, size, keyword));
    }

    @PostMapping("/categories")
    public Result<Category> createCategory(@RequestBody Category category) {
        return Result.success(categoryService.createCategory(category));
    }

    @PutMapping("/categories")
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    // 商品管理
    @GetMapping("/products")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.getProducts(page, size, keyword));
    }

    @PostMapping("/products")
    public Result<Product> createProduct(@RequestBody Product product) {
        return Result.success(productService.createProduct(product));
    }

    @PutMapping("/products/{id}")
    public Result<Void> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success();
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/products/{id}/status")
    public Result<Void> updateProductStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> requestBody) {
        Integer status = requestBody.get("status");

        if (status == 0) {
            productService.enableUser(id);
        } else {
            productService.disableUser(id);
        }
        return Result.success();
    }

    @PutMapping("/products/{id}/stock")
    public Result<Void> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        productService.updateStock(id, stock);
        return Result.success();
    }

    // 订单管理
    @GetMapping("/orders")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(orderService.getAllOrders(page, size, keyword));
    }

    @PostMapping("/orders/{orderNo}/deliver")
    public Result<Void> deliverOrder(
            @PathVariable String orderNo,
            @RequestParam String expressNo,
            @RequestParam String expressCompany) {
        orderService.deliverOrder(orderNo, expressNo, expressCompany);
        return Result.success();
    }

    @PostMapping("/orders/{orderNo}/refund")
    public Result<Void> refundOrder(@PathVariable String orderNo) {
        orderService.refundOrder(orderNo);
        return Result.success();
    }

    // 评论管理
    @GetMapping("/comments")
    public Result<Page<Comment>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(commentService.getComments(page, size, keyword));
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
} 