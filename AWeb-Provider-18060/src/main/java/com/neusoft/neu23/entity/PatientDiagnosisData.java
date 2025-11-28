package com.neusoft.neu23.entity;

public class PatientDiagnosisData {
    private String patientName;
    private String diagnosisResult;

    public PatientDiagnosisData() {}

    public PatientDiagnosisData(String patientName, String diagnosisResult) {
        this.patientName = patientName;
        this.diagnosisResult = diagnosisResult;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(String diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    @Override
    public String toString() {
        return "PatientDiagnosisData{" +
                "patientName='" + patientName + '\'' +
                ", diagnosisResult='" + diagnosisResult + '\'' +
                '}';
    }
}