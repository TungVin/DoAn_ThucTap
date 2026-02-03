package com.EduQuiz.Project_intel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

   
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/") // folder uploads/ cạnh dự án
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

    }
}
