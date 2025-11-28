package com.neusoft.neu23.dto;

import lombok.Data;
import java.util.List;

@Data
public class TransferResponse {
    private boolean success;
    private String message;
    private List<PatientSymptomDTO> response;

    public List<PatientSymptomDTO> getData(){return response;};
}