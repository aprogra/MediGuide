package com.neusoft.neu23.controller;

import com.neusoft.neu23.dto.PatientRequest;
import com.neusoft.neu23.dto.PatientSymptomDTO;
import com.neusoft.neu23.dto.TransferResponse;
import com.neusoft.neu23.entity.DoctorPatient;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.entity.PatientData;
import com.neusoft.neu23.entity.PatientDataResponse;
import com.neusoft.neu23.mapper.DoctorPatientMapper;
import com.neusoft.neu23.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/patient")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorPatientMapper doctorPatientMapper;
    
    @Autowired
    private RestTemplate restTemplate;

    // 固定的医生编号
    private static final int DOCTOR_ID = 20230001;

    /**
     * 接收病人信息并保存到数据库
     * @param patientRequest 病人信息请求对象
     * @return 保存结果
     */
    @PostMapping("/save")
    public String savePatient(@RequestBody PatientRequest patientRequest) {
        try {
            // 创建Patient对象并保存
            Patient patient = new Patient();
            patient.setName(patientRequest.getName());
            patient.setDescriptions(patientRequest.getDescriptions());
            
            // 插入病人信息
            patientMapper.insert(patient);
            
            // 获取插入后的病人编号
            Integer patientId = patient.getNum();
            
            // 创建医生与病人的关联记录
            DoctorPatient doctorPatient = new DoctorPatient();
            doctorPatient.setDocId(DOCTOR_ID);
            doctorPatient.setPid(patientId);
            
            // 插入关联记录
            doctorPatientMapper.insert(doctorPatient);
            
            return "病人信息保存成功，病人编号：" + patientId;
        } catch (Exception e) {
            e.printStackTrace();
            return "病人信息保存失败：" + e.getMessage();
        }
    }
    
    /**
     * 从provider获取多个patient数据并保存到数据库
     * @return 处理结果
     */
    @GetMapping("/fetchAndSave")
    public String fetchAndSavePatients() {
        try {
            // 使用服务名调用provider的/api/data/transfer接口
            String url = "http://web-provider/api/data/transfer";
            System.out.println("调用URL: " + url);
            
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建空的请求体
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            
            // 调用provider服务，获取包装对象（使用POST方法）
            TransferResponse response = restTemplate.postForObject(url, entity, TransferResponse.class);
            
            // 输出接收到的原始数据
            System.out.println("接收到的原始数据: " + response);
            
            // 从包装对象中提取数据
            List<PatientSymptomDTO> patients = response != null ? response.getData() : null;
            
            // 输出解析后的数据
            System.out.println("解析后的患者数据: " + patients);
            
            // 处理每个PatientData对象
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("获取并保存病人信息结果: ");
            
            if (patients != null && !patients.isEmpty()) {
                for (PatientSymptomDTO patientSymptomDTO : patients) {
                    // 创建Patient对象并保存
                    Patient patient = new Patient();
                    patient.setName(patientSymptomDTO.getPatientName());
                    patient.setDescriptions(patientSymptomDTO.getSymptoms());
                    
                    // 插入病人信息
                    patientMapper.insert(patient);
                    
                    // 获取插入后的病人编号
                    Integer patientId = patient.getNum();
                    
                    // 创建医生与病人的关联记录
                    DoctorPatient doctorPatient = new DoctorPatient();
                    doctorPatient.setDocId(DOCTOR_ID);
                    doctorPatient.setPid(patientId);
                    
                    // 插入关联记录
                    doctorPatientMapper.insert(doctorPatient);
                    
                    System.out.println("病人信息已保存: " + patientSymptomDTO.getPatientName() + ", ID: " + patientId);
                    responseBuilder.append("[").append(patientSymptomDTO.getPatientName()).append(",ID:").append(patientId).append("]已保存; ");
                }
            } else {
                responseBuilder.append("无病人数据返回");
            }
            
            return responseBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "获取并保存病人信息失败: " + e.getMessage();
        }
    }
    
    /**
     * 接收外部传入的患者数据（新格式）
     * @param transferResponse 传输响应对象
     * @return 处理结果
     */
    @PostMapping("/receive-data")
    public String receivePatientData(@RequestBody TransferResponse transferResponse) {
        try {
            // 输出接收到的原始数据
            System.out.println("接收到的原始数据: " + transferResponse);
            
            // 检查传输是否成功
            if (!transferResponse.isSuccess()) {
                return "数据传输失败: " + transferResponse.getMessage();
            }
            
            // 获取患者症状数据列表
            List<PatientSymptomDTO> patientSymptoms = transferResponse.getResponse();
            
            // 输出解析后的数据
            System.out.println("解析后的患者症状数据: " + patientSymptoms);
            
            // 处理每个PatientSymptomDTO对象
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("接收并保存病人信息结果: ");
            
            if (patientSymptoms != null && !patientSymptoms.isEmpty()) {
                for (PatientSymptomDTO patientSymptom : patientSymptoms) {
                    // 创建Patient对象并保存
                    Patient patient = new Patient();
                    patient.setName(patientSymptom.getPatientName());
                    patient.setDescriptions(patientSymptom.getSymptoms());
                    
                    // 插入病人信息
                    patientMapper.insert(patient);
                    
                    // 获取插入后的病人编号
                    Integer patientId = patient.getNum();
                    
                    // 创建医生与病人的关联记录
                    DoctorPatient doctorPatient = new DoctorPatient();
                    doctorPatient.setDocId(DOCTOR_ID);
                    doctorPatient.setPid(patientId);
                    
                    // 插入关联记录
                    doctorPatientMapper.insert(doctorPatient);
                    
                    System.out.println("病人信息已保存: " + patientSymptom.getPatientName() + ", ID: " + patientId);
                    responseBuilder.append("[").append(patientSymptom.getPatientName()).append(",ID:").append(patientId).append("]已保存; ");
                }
            } else {
                responseBuilder.append("无病人数据传入");
            }
            
            return responseBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "接收并保存病人信息失败: " + e.getMessage();
        }
    }
}