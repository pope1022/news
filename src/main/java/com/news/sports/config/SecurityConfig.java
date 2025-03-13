package com.news.sports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

//    // 使用构造函数注入，并标记为延迟加载
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 关闭
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/api/auth/**").permitAll()  // 匹配 /api/auth/** 路径，允许所有访问
                        .antMatchers("/api/news/list").permitAll()  // 匹配 /api/news/list 路径，允许所有访问
                        .antMatchers("/api/news/detail/**").permitAll()  // 匹配 /api/news/detail/** 路径，允许所有访问
                        .antMatchers("/api/admin/**").permitAll()  // 匹配 /api/admin/** 路径，要求角色为 ADMIN
                        .antMatchers("/uploads/**").permitAll()
                        .anyRequest().authenticated()  // 其他所有请求都需要认证
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // 添加 JWT 认证过滤器

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 配置 CORS
//                .csrf().disable()  // 禁用 CSRF
//                .authorizeRequests()
//                .antMatchers("/api/actuator/**").permitAll()  // 允许访问 Actuator
//                .antMatchers("/api/auth/**").permitAll()
//                .anyRequest().permitAll();  // 放行其他请求
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // 允许前端应用访问
//        configuration.addAllowedOrigin("http://localhost:5173");  // 前端地址
//        configuration.addAllowedMethod("GET");
//        configuration.addAllowedMethod("POST");
//        configuration.addAllowedMethod("PUT");
//        configuration.addAllowedMethod("DELETE");
//        configuration.addAllowedHeader("*");  // 允许所有请求头
//        configuration.setAllowCredentials(true);  // 是否允许携带 Cookie
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);  // 允许所有接口都支持 CORS
//        return source;
//    }
}