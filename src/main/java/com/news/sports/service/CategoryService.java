package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    
    void updateCategory(Category category);
    
    void deleteCategory(Long id);
    
    Category getCategoryById(Long id);
    
    List<Category> getAllCategories();
    
    Page<Category> getCategories(Integer page, Integer size, String keyword);
} 