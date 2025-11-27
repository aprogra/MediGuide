package com.neusoft.neu23.controller;

import com.neusoft.neu23.entity.Test;
import com.neusoft.neu23.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin
public class NacosDemoContoller {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private TestMapper testMapper;

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
        Test[] resultArray = restTemplate.getForObject(url, Test[].class);
        List<Test> result = Arrays.asList(resultArray);
        return "调用结果: " + result.toString();
    }
    
    // 使用RestTemplate + LoadBalancer通过服务名调用服务
    @GetMapping("/c/servicecall")
    public String serviceCall() {
        // 直接使用服务名调用，LoadBalancer会自动进行负载均衡
        String url = "http://web-provider/s/s1";
        System.out.println("使用服务名调用: " + url);
        
        // 调用provider服务，获取多个Dept对象
        Test[] resultArray = restTemplate.getForObject(url, Test[].class);
        List<Test> results = Arrays.asList(resultArray);
        
        // 处理每个Dept对象
        StringBuilder response = new StringBuilder();
        response.append("服务名调用结果: ");
        
        if (results != null && !results.isEmpty()) {
            for (Test result : results) {
                // 检查是否已存在相同数据
                int count = testMapper.countByDeptnoAndDname(result.getDeptno(), result.getDname());
                
                if (count == 0) {
                    // 数据不存在，保存到test表
                    Test test = new Test();
                    test.setDeptno(result.getDeptno());
                    test.setDname(result.getDname());
                    testMapper.insert(test);
                    System.out.println("数据已保存到test表: " + result.getDeptno() + ", " + result.getDname());
                    response.append("[").append(result.getDeptno()).append(",").append(result.getDname()).append("]已保存; ");
                } else {
                    System.out.println("数据已存在，跳过保存: " + result.getDeptno() + ", " + result.getDname());
                    response.append("[").append(result.getDeptno()).append(",").append(result.getDname()).append("]已存在; ");
                }
            }
        } else {
            response.append("无数据返回");
        }
        
        return response.toString();
    }
}