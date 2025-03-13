package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.News;
import com.news.sports.mapper.NewsMapper;
import com.news.sports.service.NewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsMapper newsMapper) {
        this.newsMapper = newsMapper;
    }

    @Override
    @Transactional
    public News createNews(News news) {
        newsMapper.insert(news);
        return news;
    }

    @Override
    @Transactional
    public void updateNews(News news) {
        newsMapper.updateById(news);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        newsMapper.deleteById(id);
    }

    @Override
    public News getNewsById(Long id) {
        return newsMapper.selectById(id);
    }

    @Override
    public Page<News> getNewsList(Integer page, Integer size, String keyword, Long categoryId) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword)
                    .or()
                    .like(News::getContent, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(News::getCategoryId, categoryId);
        }
        return newsMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void publishNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        news.setStatus(1);
        newsMapper.updateById(news);
    }

    @Override
    @Transactional
    public void unpublishNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        news.setStatus(2);
        newsMapper.updateById(news);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        newsMapper.incrementViewCount(id);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long id) {
        newsMapper.incrementLikeCount(id);
    }

    @Override
    @Transactional
    public void incrementCommentCount(Long id) {
        newsMapper.incrementCommentCount(id);
    }

    @Override
    public Page<News> getHotNews(Integer page, Integer size) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1)
                .orderByDesc(News::getViewCount)
                .orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<News> getLatestNews(Integer page, Integer size) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1)
                .orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(new Page<>(page, size), wrapper);
    }
} 