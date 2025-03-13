package com.news.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String coverImage;
    
    private BigDecimal price;
    
    private Integer points;      // 所需积分
    
    private Integer stock;       // 库存
    
    private Integer salesCount;  // 销量
    
    private Integer status;      // 0: 下架, 1: 上架
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;     // 0: 未删除, 1: 已删除
} 