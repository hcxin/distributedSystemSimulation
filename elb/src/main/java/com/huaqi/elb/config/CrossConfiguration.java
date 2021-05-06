package com.huaqi.elb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 类的功能描述：
 * 允许跨域
 *
 * @ClassName: CrossConfiguration
 * @Author haichen
 * @Date 2020-07-02 02:00:50
 */
@Configuration
public class CrossConfiguration {

    @Bean
    public WebMvcConfigurer crossConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*")
                        .allowedMethods("*").allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(7200L);
            }
        };
    }
}