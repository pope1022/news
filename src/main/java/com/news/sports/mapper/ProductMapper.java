package com.news.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.news.sports.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Update("UPDATE product SET sales_count = sales_count + 1 WHERE id = #{id}")
    void incrementSalesCount(Long id);
} 