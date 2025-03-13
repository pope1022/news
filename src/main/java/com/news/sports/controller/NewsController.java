package com.news.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.common.Result;
import com.news.sports.entity.News;
import com.news.sports.service.NewsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<News> createNews(@RequestBody News news) {
        return Result.success(newsService.createNews(news));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateNews(@RequestBody News news) {
        newsService.updateNews(news);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<News> getNewsById(@PathVariable Long id) {
        newsService.incrementViewCount(id);
        return Result.success(newsService.getNewsById(id));
    }

    @GetMapping("/list")
    public Result<Page<News>> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(newsService.getNewsList(page, size, keyword, categoryId));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> publishNews(@PathVariable Long id) {
        newsService.publishNews(id);
        return Result.success();
    }

    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> unpublishNews(@PathVariable Long id) {
        newsService.unpublishNews(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<Void> likeNews(@PathVariable Long id) {
        newsService.incrementLikeCount(id);
        return Result.success();
    }

    @GetMapping("/hot")
    public Result<Page<News>> getHotNews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(newsService.getHotNews(page, size));
    }

    @GetMapping("/latest")
    public Result<Page<News>> getLatestNews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(newsService.getLatestNews(page, size));
    }
} 