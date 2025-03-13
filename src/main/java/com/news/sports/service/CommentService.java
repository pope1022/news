package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment);
    void updateComment(Comment comment);
    void deleteComment(Long id);
    Comment getCommentById(Long id);
    List<Comment> getCommentsByNewsId(Long newsId);
    Page<Comment> getComments(Integer page, Integer size, String keyword);
    void likeComment(Long id);
} 