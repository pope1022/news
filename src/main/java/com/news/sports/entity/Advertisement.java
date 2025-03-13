package com.news.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("advertisement")
public class Advertisement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String image;
    
    private String url;
    
    private String position;
    
    private Integer sort;
    
    private Integer status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 