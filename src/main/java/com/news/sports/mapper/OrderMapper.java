package com.news.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.news.sports.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
} 