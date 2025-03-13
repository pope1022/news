package com.news.sports.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.sports.dto.LoginRequest;
import com.news.sports.dto.RegisterRequest;
import com.news.sports.entity.User;

public interface UserService {
    User register(RegisterRequest request);
    
    String login(LoginRequest request);
    
    User getCurrentUser();

    User getCurrentUserNoToken(String username);
    
    void updateUser(User user);
    
    void changePassword(String oldPassword, String newPassword);
    
    void updatePoints(Long userId, Integer points);
    
    Page<User> getUsers(Integer page, Integer size, String keyword);
    
    void enableUser(Long userId);
    
    void disableUser(Long userId);
    
    void deleteUser(Long userId);
} 