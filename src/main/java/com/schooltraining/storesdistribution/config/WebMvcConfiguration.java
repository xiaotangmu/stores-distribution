package com.schooltraining.storesdistribution.config;

import com.schooltraining.storesdistribution.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
 
	@Autowired
    AuthInterceptor authInterceptor;
	
	@Override
    protected void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/error", "/index.html", "/assets/**");
        super.addInterceptors(registry);
    }
 
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {//WebMvcConfigurationSupport 使自动配置的静态资源路径失效
        registry.addResourceHandler("/**")
        		.addResourceLocations("classpath:/static/")
        		.addResourceLocations("classpath:/templates/")
        		.addResourceLocations("classpath:/resources/")
        		.addResourceLocations("classpath:/");
        super.addResourceHandlers(registry);
    }
}
