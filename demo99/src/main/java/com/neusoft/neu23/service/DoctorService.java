package com.neusoft.neu23.service;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.Patient;

import java.util.List;

public interface DoctorService {
    Doctor getByID(int id);
    
    List<Patient> getPatientsByDoctorId(int doctorId);
    
    Patient getMinNumPatientByDoctorId(int doctorId);
    
    /**
     * 删除医生与患者的关联关系
     * @param doctorId 医生ID
     * @param patientId 患者ID
     * @return 删除结果：true-成功，false-失败
     */
    boolean removeDoctorPatientRelation(int doctorId, int patientId);
    
    /**
     * 医生登录验证
     * @param id 医生ID
     * @param username 医生用户名（对应doc_name字段）
     * @return 验证成功返回医生信息，验证失败返回null
     */
    Doctor login(int id, String username);
}