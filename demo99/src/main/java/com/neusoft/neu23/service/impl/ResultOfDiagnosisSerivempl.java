package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.Drug;
import com.neusoft.neu23.entity.ResultOfDiagnosis;
import com.neusoft.neu23.mapper.ResultOfDiagnosisMapper;
import com.neusoft.neu23.service.ResultOfDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        // 根据诊断结果推荐药品 - 基于AI分析药物类型相似性
        List<String> recommendedDrugs = new ArrayList<>();
        
        // AI分析诊断结果应该使用的药物类型（模拟AI分析过程）
        String inferredDrugCategory = inferDrugCategory(diagnosisResult);
        
        // 根据药品类别进行筛选
        for (Drug drug : allDrugs) {
            String category = drug.getCategory();
            if (category != null && isCategorySimilar(category, inferredDrugCategory)) {
                recommendedDrugs.add(drug.getName());
            }
        }
        
        // 如果没有找到相似类别，返回前几个药品作为默认推荐
        if (recommendedDrugs.isEmpty() && !allDrugs.isEmpty()) {
            for (int i = 0; i < Math.min(3, allDrugs.size()); i++) {
                recommendedDrugs.add(allDrugs.get(i).getName());
            }
        }
        
        return recommendedDrugs;
    }
    
    /**
     * AI分析诊断结果应该使用的药物类型
     * @param diagnosisResult 诊断结果
     * @return 推断的药物类别
     */
    private String inferDrugCategory(String diagnosisResult) {
        // 这里可以调用AI模型进行分析，现在使用简单的关键词匹配模拟
        Map<String, String> keywordToCategory = new HashMap<>();
        keywordToCategory.put("感冒", "感冒药");
        keywordToCategory.put("发热", "退烧药");
        keywordToCategory.put("咳嗽", "止咳药");
        keywordToCategory.put("头痛", "止痛药");
        keywordToCategory.put("消炎", "消炎药");
        keywordToCategory.put("抗生素", "抗生素");
        keywordToCategory.put("过敏", "抗过敏药");
        keywordToCategory.put("高血压", "降压药");
        keywordToCategory.put("糖尿病", "降糖药");
        
        for (Map.Entry<String, String> entry : keywordToCategory.entrySet()) {
            if (diagnosisResult.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 默认返回常见药物类别
        return "常用药物";
    }
    
    /**
     * 判断药品类别是否相似
     * @param drugCategory 药品的实际类别
     * @param inferredCategory AI推断的类别
     * @return 是否相似
     */
    private boolean isCategorySimilar(String drugCategory, String inferredCategory) {
        // 这里可以使用更复杂的相似度算法，现在使用简单的包含关系判断
        return drugCategory.contains(inferredCategory) || inferredCategory.contains(drugCategory);
    }
}