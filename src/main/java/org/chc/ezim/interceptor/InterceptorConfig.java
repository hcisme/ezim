package org.chc.ezim.interceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private AccessInterceptor accessInterceptor;

    @Resource
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(logInterceptor)
                .addPathPatterns("/**");

        registry
                .addInterceptor(accessInterceptor)
                .addPathPatterns("/**");
    }
}
