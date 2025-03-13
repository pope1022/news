package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Category;
import com.news.sports.mapper.CategoryMapper;
import com.news.sports.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort));
    }

    @Override
    public Page<Category> getCategories(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Category::getName, keyword);
        }
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectPage(new Page<>(page, size), wrapper);
    }
} 