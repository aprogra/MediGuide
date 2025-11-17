package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.Drug;
import com.neusoft.neu23.entity.ResultOfDiagnosis;
import com.neusoft.neu23.mapper.ResultOfDiagnosisMapper;
import com.neusoft.neu23.service.ResultOfDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultOfDiagnosisSerivempl extends ServiceImpl<ResultOfDiagnosisMapper, ResultOfDiagnosis> implements ResultOfDiagnosisService {
    
    @Autowired
    private com.neusoft.neu23.service.DrugService drugService;
    
    @Override
    public Integer saveDiagnosisResult(ResultOfDiagnosis resultOfDiagnosis) {
        int i = baseMapper.insert(resultOfDiagnosis);
        if (i > 0) {
            return 1; // 保存成功
        }
        return -1; // 保存失败
    }
    
    @Override
    public List<String> recommendDrugs(String diagnosisResult) {
        // 获取所有药品信息
        List<Drug> allDrugs = drugService.list();
        
        // 根据诊断结果推荐药品
        List<String> recommendedDrugs = new ArrayList<>();
        
        // 简单匹配逻辑：检查诊断结果中是否包含药品的适用症状
        for (Drug drug : allDrugs) {
            String applicableSymptoms = drug.getApplicableSymptoms();
            if (applicableSymptoms != null && diagnosisResult.contains(applicableSymptoms)) {
                recommendedDrugs.add(drug.getName());
            }
        }
        
        // 如果没有找到精确匹配，返回前几个药品作为默认推荐
        if (recommendedDrugs.isEmpty() && !allDrugs.isEmpty()) {
            for (int i = 0; i < Math.min(3, allDrugs.size()); i++) {
                recommendedDrugs.add(allDrugs.get(i).getName());
            }
        }
        
        return recommendedDrugs;
    }
}