package com.news.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private String coverImage;
    
    private Long categoryId;  // 新闻类别ID
    
    private Long authorId;    // 作者ID
    
    private Integer viewCount;    // 浏览量
    
    private Integer likeCount;    // 点赞数
    
    private Integer commentCount; // 评论数
    
    private Integer status;   // 0: 草稿, 1: 已发布, 2: 已下架
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;  // 0: 未删除, 1: 已删除
} 