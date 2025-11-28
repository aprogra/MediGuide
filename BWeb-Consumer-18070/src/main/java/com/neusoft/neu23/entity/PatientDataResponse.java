package com.neusoft.neu23.entity;

import java.util.List;

public class PatientDataResponse {
    private List<PatientData> data;
    
    // Getters and Setters
    public List<PatientData> getData() {
        return data;
    }
    
    public void setData(List<PatientData> data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "PatientDataResponse{" +
                "data=" + data +
                '}';
    }
}