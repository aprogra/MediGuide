package com.neusoft.neu23;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
// （3/3）启动服务注册中心
@EnableDiscoveryClient
@MapperScan("com.neusoft.neu23.mapper")
public class BWebConsumer18070App
{
    public static void main( String[] args )
    {
        SpringApplication.run(BWebConsumer18070App.class, args);
        System.out.println( "Hello World!" );
    }
}
