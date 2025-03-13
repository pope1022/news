package com.news.sports.controller;

import com.news.sports.common.Result;
import com.news.sports.dto.JwtResponse;
import com.news.sports.dto.LoginRequest;
import com.news.sports.dto.RegisterRequest;
import com.news.sports.entity.User;
import com.news.sports.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("🔹 收到登录请求，用户名：" + request.getUsername());
        User user = userService.getCurrentUserNoToken( request.getUsername());
        System.out.println(user);
        if (user == null) {
            // 如果用户不存在，直接返回正常的响应，但在消息中告知用户没有找到该用户名
            System.out.println("🔹 用户不存在！");
            return Result.error("用户名不存在");
        }

        // 如果用户存在，生成 JWT token
        String token = userService.login(request);
        return Result.success(new JwtResponse(token, user.getUsername(), user.getNickname(), user.getRole()));
    }

    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
} 