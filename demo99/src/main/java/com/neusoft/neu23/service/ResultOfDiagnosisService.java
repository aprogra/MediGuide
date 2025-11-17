package com.neusoft.neu23.service;

import com.neusoft.neu23.entity.ResultOfDiagnosis;
import java.util.List;

public interface ResultOfDiagnosisService {
    Integer saveDiagnosisResult(ResultOfDiagnosis resultOfDiagnosis);
    
    /**
     * 根据诊断结果推荐药物
     * @param diagnosisResult 诊断结果
     * @return 推荐的药物列表
     */
    List<String> recommendDrugs(String diagnosisResult);
}