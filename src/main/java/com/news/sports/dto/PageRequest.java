package com.news.sports.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private String sortField;
    private String sortOrder;  // asc, desc
} 