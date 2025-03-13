package com.news.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.news.sports.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
} 