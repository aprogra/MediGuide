package com.neusoft.neu23.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("drugs")
public class Drug {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("category")
    private String category;

    @TableField("introduction")
    private String introduction;

    @TableField("usage_method")
    private String usageMethod;

    @TableField("applicable_symptoms")
    private String applicableSymptoms;

    @TableField("advantages")
    private String advantages;

    @TableField("side_effects")
    private String sideEffects;

    public Drug() {}

    public Drug(Integer id, String name, String category, String introduction, String usageMethod, 
                String applicableSymptoms, String advantages, String sideEffects) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.introduction = introduction;
        this.usageMethod = usageMethod;
        this.applicableSymptoms = applicableSymptoms;
        this.advantages = advantages;
        this.sideEffects = sideEffects;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUsageMethod() {
        return usageMethod;
    }

    public void setUsageMethod(String usageMethod) {
        this.usageMethod = usageMethod;
    }

    public String getApplicableSymptoms() {
        return applicableSymptoms;
    }

    public void setApplicableSymptoms(String applicableSymptoms) {
        this.applicableSymptoms = applicableSymptoms;
    }

    public String getAdvantages() {
        return advantages;
    }

    public void setAdvantages(String advantages) {
        this.advantages = advantages;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }
}