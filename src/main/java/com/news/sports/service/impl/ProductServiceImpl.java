package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Product;
import com.news.sports.mapper.ProductMapper;
import com.news.sports.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    @Transactional
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Page<Product> getProducts(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword);
        }
        wrapper.eq(Product::getStatus, 1) // 只查询上架的商品
                .orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void updateStock(Long id, Integer stock) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStock(stock);
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void incrementSalesCount(Long id) {
        productMapper.incrementSalesCount(id);
    }

    @Override
    @Transactional
    public void putOnSale(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(1);
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void takeOffSale(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(0);
        productMapper.updateById(product);
    }

    @Override
    public boolean checkStock(Long id, Integer quantity) {
        Product product = productMapper.selectById(id);
        return product != null && product.getStock() >= quantity;
    }

    @Override
    public Page<Product> getHotProducts(Integer page, Integer size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getSalesCount)
                .orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }
} 