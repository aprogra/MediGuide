package com.neusoft.neu23.controller;

import com.neusoft.neu23.entity.Dept;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/s")
@CrossOrigin
public class DemoContoller {
    @Value("${server.port}")
    private String port;

    @RequestMapping("/s1")
    public List<Dept> s1(){
        List<Dept> depts = new ArrayList<>();
        
        // 创建多个Dept对象
        Dept dept1 = new Dept();
        dept1.setDeptno(port);
        dept1.setDname("清华大学");
        
        Dept dept2 = new Dept();
        dept2.setDeptno(port + "-001");
        dept2.setDname("北京大学");
        
        Dept dept3 = new Dept();
        dept3.setDeptno(port + "-002");
        dept3.setDname("复旦大学");
        
        depts.add(dept1);
        depts.add(dept2);
        depts.add(dept3);
        
        return depts;
    }
    
    // 新增API端点，用于返回多个patient数据
    @RequestMapping("/api/data/transfer")
    public List<PatientData> getPatientData(){
        List<PatientData> patients = new ArrayList<>();
        
        // 创建多个PatientData对象
        PatientData patient1 = new PatientData();
        patient1.setId(1);
        patient1.setName("张三");
        patient1.setDescription("感冒发烧");
        
        PatientData patient2 = new PatientData();
        patient2.setId(2);
        patient2.setName("李四");
        patient2.setDescription("咳嗽头痛");
        
        PatientData patient3 = new PatientData();
        patient3.setId(3);
        patient3.setName("王五");
        patient3.setDescription("胃痛腹泻");
        
        patients.add(patient1);
        patients.add(patient2);
        patients.add(patient3);
        
        return patients;
    }
    
    // 内部类，用于表示Patient数据
    public static class PatientData {
        private int id;
        private String name;
        private String description;
        
        // Getters and Setters
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}