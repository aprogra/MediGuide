package com.neusoft.neu23.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("medicine_result")
public class MedicineResult {
    @TableField("num")
    private Integer num;

    @TableField("id")
    private Integer id;

    @TableField("doc_name")
    private String docName;

    @TableField("medicine")
    private String medicine;

    @TableField("medicine_helper")
    private String medicineHelper;

    public MedicineResult() {}

    public MedicineResult(Integer num, Integer id, String docName, String medicine, String medicineHelper) {
        this.num = num;
        this.id = id;
        this.docName = docName;
        this.medicine = medicine;
        this.medicineHelper = medicineHelper;
    }

    // Getters and Setters
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getMedicineHelper() {
        return medicineHelper;
    }

    public void setMedicineHelper(String medicineHelper) {
        this.medicineHelper = medicineHelper;
    }
}