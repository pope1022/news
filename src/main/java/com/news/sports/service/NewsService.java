package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.News;

public interface NewsService {
    News createNews(News news);
    
    void updateNews(News news);
    
    void deleteNews(Long id);
    
    News getNewsById(Long id);
    
    Page<News> getNewsList(Integer page, Integer size, String keyword, Long categoryId);
    
    void publishNews(Long id);
    
    void unpublishNews(Long id);
    
    void incrementViewCount(Long id);
    
    void incrementLikeCount(Long id);
    
    void incrementCommentCount(Long id);
    
    Page<News> getHotNews(Integer page, Integer size);
    
    Page<News> getLatestNews(Integer page, Integer size);
} 