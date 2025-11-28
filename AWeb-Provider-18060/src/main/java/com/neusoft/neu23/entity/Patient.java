package com.neusoft.neu23.entity;

public class Patient {
    private Integer num;
    private String name;
    private String descriptions;

    public Patient() {}

    public Patient(Integer num, String name, String descriptions) {
        this.num = num;
        this.name = name;
        this.descriptions = descriptions;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }
}