package com.pjb.kindergarten_suggestion.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry) {
        Path uploadPathDir = Paths.get(uploadDir);
        String uploadPath = uploadPathDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath + "/");
    }
}