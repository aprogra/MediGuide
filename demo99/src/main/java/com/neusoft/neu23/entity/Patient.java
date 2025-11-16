package com.neusoft.neu23.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

public class Patient {
    @TableId(type = IdType.AUTO ,value = "num")
    private Integer num;

    @TableField("name")
    private String name;

    @TableField("descriptions")
    private String descriptions;

    public Patient() { }

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

    public String getDescription() {
        return descriptions;
    }

    public void setDescription(String descriptions) {
        this.descriptions = descriptions;
    }
}
