package com.neusoft.neu23.dto;

import lombok.Data;

@Data
public class PatientSymptomDTO {
    private Integer patientId;
    private String patientName;
    private String symptoms;
}