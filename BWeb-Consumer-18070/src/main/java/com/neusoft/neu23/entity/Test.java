package com.neusoft.neu23.entity;

import lombok.Data;

@Data
public class Test {
    private String deptno;
    private String dname;

    public Test() { }

    public Test(String deptno, String dname) {
        this.deptno = deptno;
        this.dname = dname;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }
}