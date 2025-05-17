package org.example.coursedesign.config;

import org.example.coursedesign.intercepter.LoginIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginIntercepter loginIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(loginIntercepter)
        .excludePathPatterns("/user/login","/user/register","/userPicture/**","/product/getProduct","/product/categories","/productPicture/**");
    }
}
