package com.neusoft.neu23.entity;

public class ResultOfDiagnosis {
    private Integer num;
    private Integer id;
    private String docName;
    private String result;

    public ResultOfDiagnosis() {}

    public ResultOfDiagnosis(Integer num, Integer id, String docName, String result) {
        this.num = num;
        this.id = id;
        this.docName = docName;
        this.result = result;
    }

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}