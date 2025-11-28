package com.neusoft.neu23.controller;

import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.entity.ResultOfDiagnosis;
import com.neusoft.neu23.entity.PatientDiagnosisData;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/data")
@CrossOrigin
public class DataTransferController {
    
    // 模拟数据库中的病人数据
    private List<Patient> getPatientsFromDatabase() {
        List<Patient> patients = new ArrayList<>();
        
        Patient patient1 = new Patient();
        patient1.setNum(1);
        patient1.setName("张三");
        patient1.setDescriptions("感冒发烧");
        
        Patient patient2 = new Patient();
        patient2.setNum(2);
        patient2.setName("李四");
        patient2.setDescriptions("咳嗽头痛");
        
        Patient patient3 = new Patient();
        patient3.setNum(3);
        patient3.setName("王五");
        patient3.setDescriptions("胃痛腹泻");
        
        patients.add(patient1);
        patients.add(patient2);
        patients.add(patient3);
        
        return patients;
    }
    
    // 模拟数据库中的诊断结果数据
    private List<ResultOfDiagnosis> getDiagnosisResultsFromDatabase() {
        List<ResultOfDiagnosis> results = new ArrayList<>();
        
        ResultOfDiagnosis result1 = new ResultOfDiagnosis();
        result1.setNum(1);
        result1.setId(101);
        result1.setDocName("张医生");
        result1.setResult("普通感冒，建议多喝水，注意休息");
        
        ResultOfDiagnosis result2 = new ResultOfDiagnosis();
        result2.setNum(2);
        result2.setId(102);
        result2.setDocName("李医生");
        result2.setResult("病毒性感冒，开药治疗");
        
        ResultOfDiagnosis result3 = new ResultOfDiagnosis();
        result3.setNum(3);
        result3.setId(103);
        result3.setDocName("王医生");
        result3.setResult("急性肠胃炎，饮食清淡，药物治疗");
        
        results.add(result1);
        results.add(result2);
        results.add(result3);
        
        return results;
    }
    
    // API端点，用于返回多个patient姓名以及诊断结果数据
    @RequestMapping("/transfer")
    public List<PatientDiagnosisData> getPatientDiagnosisData(){
        // 获取病人数据
        List<Patient> patients = getPatientsFromDatabase();
        
        // 获取诊断结果数据
        List<ResultOfDiagnosis> diagnosisResults = getDiagnosisResultsFromDatabase();
        
        // 创建映射以便快速查找病人信息
        Map<Integer, String> patientMap = new HashMap<>();
        for (Patient patient : patients) {
            patientMap.put(patient.getNum(), patient.getName());
        }
        
        // 组合数据
        List<PatientDiagnosisData> combinedData = new ArrayList<>();
        for (ResultOfDiagnosis diagnosis : diagnosisResults) {
            String patientName = patientMap.get(diagnosis.getNum());
            if (patientName != null) {
                PatientDiagnosisData data = new PatientDiagnosisData();
                data.setPatientName(patientName);
                data.setDiagnosisResult(diagnosis.getResult());
                combinedData.add(data);
            }
        }
        System.out.println(combinedData);
        return combinedData;
    }
}