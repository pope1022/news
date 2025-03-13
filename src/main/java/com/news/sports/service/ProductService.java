package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Product;

public interface ProductService {
    Product createProduct(Product product);
    
    void updateProduct(Product product);
    
    void deleteProduct(Long id);
    
    Product getProductById(Long id);
    
    Page<Product> getProducts(Integer page, Integer size, String keyword);
    
    void updateStock(Long id, Integer stock);
    
    void incrementSalesCount(Long id);
    
    void putOnSale(Long id);
    
    void takeOffSale(Long id);
    
    boolean checkStock(Long id, Integer quantity);
    
    Page<Product> getHotProducts(Integer page, Integer size);
} 