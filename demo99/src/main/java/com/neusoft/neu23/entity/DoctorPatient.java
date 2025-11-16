package com.neusoft.neu23.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("doctor_patient")
public class DoctorPatient {
    @TableField("doc_id")
    private Integer docId;

    @TableField("pid")
    private Integer pid;

    public DoctorPatient() {
    }

    public DoctorPatient(Integer docId, Integer pid) {
        this.docId = docId;
        this.pid = pid;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }
}

