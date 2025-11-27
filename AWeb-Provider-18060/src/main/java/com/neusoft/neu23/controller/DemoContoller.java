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
}