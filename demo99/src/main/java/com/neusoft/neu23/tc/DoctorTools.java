package com.neusoft.neu23.tc;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.entity.ResultOfDiagnosis;
import com.neusoft.neu23.service.DoctorService;
import com.neusoft.neu23.service.ResultOfDiagnosisService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorTools {
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private ResultOfDiagnosisService resultOfDiagnosisService;

    @Tool(description = "根据医生输入的id来查询对应的医生信息")
    public Doctor getByID(
            @ToolParam(description = "医生的id，参数id代表医生的id")Integer id
    ){
        return doctorService.getByID(id);
    }
    
    @Tool(description = "获取患者病情描述工具，根据医生ID查询该医生关联的序号最小的患者信息，包括患者姓名和病情描述，用于生成初步诊断报告")
    public String getPatientDescriptions(
            @ToolParam(description = "医生的ID，参数doctorId代表医生的ID") Integer doctorId
    ) {
        Patient patient = doctorService.getMinNumPatientByDoctorId(doctorId);
        
        if (patient == null) {
            return "该医生目前没有关联的患者信息。";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("患者信息如下：\n");
        result.append(String.format("  患者编号：%d\n", patient.getNum()));
        result.append(String.format("  患者姓名：%s\n", patient.getName()));
        result.append(String.format("  病情描述：%s\n", patient.getDescription() != null ? patient.getDescription() : "暂无描述"));
        
        return result.toString();
    }
    
    @Tool(description = "保存诊断结果工具，将医生的最终诊断信息保存到数据库中，包括患者ID、诊断医生ID、诊断医生姓名和最终诊断结果。保存之后并进行药物的推荐，通过分析药物的种类、介绍、适用症状、优势和副作用来整体进行药物推荐")
    public String saveDiagnosisResult(
            @ToolParam(description = "患者编号，参数patientId代表患者的编号") Integer patientId,
            @ToolParam(description = "诊断医生的ID，参数doctorId代表医生的编号") Integer doctorId,
            @ToolParam(description = "诊断医生的姓名，参数doctorName代表医生的姓名") String doctorName,
            @ToolParam(description = "最终诊断结果，参数result代表医生的诊断结论") String result
    ) {
        ResultOfDiagnosis resultOfDiagnosis = new ResultOfDiagnosis(patientId, doctorId, doctorName, result);
        Integer saveResult = resultOfDiagnosisService.saveDiagnosisResult(resultOfDiagnosis);
        
        if (saveResult > 0) {
            // 保存成功后推荐药物
            List<String> recommendedDrugs = resultOfDiagnosisService.recommendDrugs(result);
            
            StringBuilder response = new StringBuilder();
            response.append(String.format("诊断结果保存成功！\n患者编号：%d\n诊断医生：%s（ID：%d）\n最终诊断：%s\n", 
                    patientId, doctorName, doctorId, result));
            
            if (!recommendedDrugs.isEmpty()) {
                response.append("\n根据诊断结果，推荐以下药物：\n");
                for (int i = 0; i < recommendedDrugs.size(); i++) {
                    response.append(String.format("%d. %s\n", i + 1, recommendedDrugs.get(i)));
                }
            } else {
                response.append("\n暂无匹配的药物推荐。\n");
            }
            
            return response.toString();
        } else {
            return "诊断结果保存失败，请检查数据是否正确。";
        }
    }
}