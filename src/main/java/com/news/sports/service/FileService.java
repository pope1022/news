package com.news.sports.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class FileService {

    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    @PostConstruct
    public void init() {
        resourceUrlProvider.getForLookupPath("/uploads/");
    }
}
