package com.news.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")  // order是MySQL关键字，需要用反引号
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;      // 订单编号
    
    private Long userId;         // 用户ID
    
    private Long productId;      // 商品ID
    
    private Integer quantity;    // 数量
    
    private BigDecimal amount;   // 订单金额
    
    private Integer payType;     // 支付方式：0现金，1积分
    
    private Integer status;      // 订单状态：0待支付，1已支付，2已发货，3已完成，4已取消
    
    private String address;      // 收货地址
    
    private String receiver;     // 收货人
    
    private String phone;        // 联系电话
    
    private String expressNo;    // 快递单号
    
    private String expressCompany; // 快递公司
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime payTime;      // 支付时间
    
    private LocalDateTime deliveryTime; // 发货时间
    
    @TableLogic
    private Integer deleted;     // 0: 未删除, 1: 已删除
} 