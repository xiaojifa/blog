package com.hzy.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/17 14:31
 */
@Data
@Component
@ConfigurationProperties("xf.config")
public class XFConfig {

    private String appId;

    private String apiSecret;

    private String apiKey;

    private String hostUrl;

    private Integer maxResponseTime;


}
