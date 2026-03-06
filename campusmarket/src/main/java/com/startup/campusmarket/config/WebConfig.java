package com.startup.campusmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** requests ko project-root/uploads/ folder se serve karega
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }
}