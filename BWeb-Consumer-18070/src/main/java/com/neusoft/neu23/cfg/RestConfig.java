package com.neusoft.neu23.cfg;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Bean
    @LoadBalanced // 添加此注解，使RestTemplate支持负载均衡和服务发现
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
