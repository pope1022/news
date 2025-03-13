package com.news.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.common.Result;
import com.news.sports.entity.Product;
import com.news.sports.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> createProduct(@RequestBody Product product) {
        return Result.success(productService.createProduct(product));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @GetMapping("/list")
    public Result<Page<Product>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.getProducts(page, size, keyword));
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        productService.updateStock(id, stock);
        return Result.success();
    }

    @PutMapping("/{id}/on-sale")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> putOnSale(@PathVariable Long id) {
        productService.putOnSale(id);
        return Result.success();
    }

    @PutMapping("/{id}/off-sale")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> takeOffSale(@PathVariable Long id) {
        productService.takeOffSale(id);
        return Result.success();
    }

    @GetMapping("/hot")
    public Result<Page<Product>> getHotProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.getHotProducts(page, size));
    }
} 