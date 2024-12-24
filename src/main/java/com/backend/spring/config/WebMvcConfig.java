//package com.backend.spring.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.CacheControl;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Định nghĩa cơ chế cache cho tài nguyên tĩnh
//        // Tắt cache bằng cách sử dụng cacheControl
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/")
//                .setCacheControl(CacheControl.noCache());
//    }
//}
