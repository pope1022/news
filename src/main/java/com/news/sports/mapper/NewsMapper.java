package com.news.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.news.sports.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
    
    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);
    
    @Update("UPDATE news SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
    
    @Update("UPDATE news SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(Long id);
} 