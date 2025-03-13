package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.entity.Comment;
import com.news.sports.mapper.CommentMapper;
import com.news.sports.service.CommentService;
import com.news.sports.service.NewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final NewsService newsService;

    public CommentServiceImpl(CommentMapper commentMapper, NewsService newsService) {
        this.commentMapper = commentMapper;
        this.newsService = newsService;
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        commentMapper.insert(comment);
        newsService.incrementCommentCount(comment.getNewsId());
        return comment;
    }

    @Override
    @Transactional
    public void updateComment(Comment comment) {
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = getCommentById(id);
        if (comment != null) {
            commentMapper.deleteById(id);
        }
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public List<Comment> getCommentsByNewsId(Long newsId) {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getNewsId, newsId)
                .orderByDesc(Comment::getCreateTime));
    }

    @Override
    public Page<Comment> getComments(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void likeComment(Long id) {
        commentMapper.incrementLikeCount(id);
    }
} 