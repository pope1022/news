package com.news.sports.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.common.Result;
import com.news.sports.entity.Comment;
import com.news.sports.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment) {
        return Result.success(commentService.createComment(comment));
    }

    @PutMapping
    public Result<Void> updateComment(@RequestBody Comment comment) {
        commentService.updateComment(comment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@PathVariable Long id) {
        return Result.success(commentService.getCommentById(id));
    }

    @GetMapping("/news/{newsId}")
    public Result<List<Comment>> getCommentsByNewsId(@PathVariable Long newsId) {
        return Result.success(commentService.getCommentsByNewsId(newsId));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Comment>> getComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(commentService.getComments(page, size, keyword));
    }

    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@PathVariable Long id) {
        commentService.likeComment(id);
        return Result.success();
    }
} 