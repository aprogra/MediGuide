package com.mediguide.patient;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 患者服务启动类
 * 
 * @author MediGuide
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mediguide.patient", "com.mediguide.common"})
@MapperScan("com.mediguide.patient.mapper")
public class PatientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

}