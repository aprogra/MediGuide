package com.neusoft.neu23.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin
public class NacosDemoContoller {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/c/nacosdemo")
    public String nacosdemo() {
        // 获取所有的web-provider服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances("web-provider");
        if (instances == null || instances.isEmpty()) {
            return "没有找到web-provider服务实例";
        }
        
        // 随机选择一个实例
        int index = new Random().nextInt(instances.size());
        ServiceInstance instance = instances.get(index);
        
        // 构建请求URL
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/s/s1";
        System.out.println("调用URL: " + url);
        
        // 调用provider服务
        String result = restTemplate.getForObject(url, String.class);
        return "调用结果: " + result;
    }
    
    // 使用RestTemplate + LoadBalancer通过服务名调用服务
    @GetMapping("/c/servicecall")
    public String serviceCall() {
        // 直接使用服务名调用，LoadBalancer会自动进行负载均衡
        String url = "http://web-provider/s/s1";
        System.out.println("使用服务名调用: " + url);
        
        // 调用provider服务
        String result = restTemplate.getForObject(url, String.class);
        return "服务名调用结果: " + result;
    }
}
