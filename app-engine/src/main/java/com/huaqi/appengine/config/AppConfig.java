package com.huaqi.appengine.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 类的功能描述：
 * 用于读取配置文件的配置
 *
 * @ClassName: AppConfig
 * @Author: haichen
 * @Date: 2020-07-01 10:45:51
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "engine.app")
public class AppConfig {
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
