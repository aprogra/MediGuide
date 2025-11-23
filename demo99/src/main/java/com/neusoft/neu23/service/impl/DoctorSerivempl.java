package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.DoctorPatient;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.mapper.DoctorMapper;
import com.neusoft.neu23.mapper.DoctorPatientMapper;
import com.neusoft.neu23.mapper.PatientMapper;
import com.neusoft.neu23.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorSerivempl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
    
    @Autowired
    private DoctorPatientMapper doctorPatientMapper;
    
    @Autowired
    private PatientMapper patientMapper;
    
    @Override
    public Doctor getByID(int id) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getId, id);
        return baseMapper.selectOne(wrapper);
    }
    
    @Override
    public List<Patient> getPatientsByDoctorId(int doctorId) {
        // 首先从 doctor_patient 表中查询该医生的所有患者ID
        LambdaQueryWrapper<DoctorPatient> dpWrapper = new LambdaQueryWrapper<>();
        dpWrapper.eq(DoctorPatient::getDocId, doctorId);
        List<DoctorPatient> doctorPatients = doctorPatientMapper.selectList(dpWrapper);
        
        if (doctorPatients == null || doctorPatients.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取患者ID列表
        List<Integer> patientIds = new ArrayList<>();
        for (DoctorPatient dp : doctorPatients) {
            patientIds.add(dp.getPid());
        }
        
        // 根据患者ID列表查询患者信息
        if (patientIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.in(Patient::getNum, patientIds);
        return patientMapper.selectList(patientWrapper);
    }
    
    @Override
    public Patient getMinNumPatientByDoctorId(int doctorId) {
        // 首先从 doctor_patient 表中查询该医生的所有患者ID
        LambdaQueryWrapper<DoctorPatient> dpWrapper = new LambdaQueryWrapper<>();
        dpWrapper.eq(DoctorPatient::getDocId, doctorId);
        List<DoctorPatient> doctorPatients = doctorPatientMapper.selectList(dpWrapper);
        
        if (doctorPatients == null || doctorPatients.isEmpty()) {
            return null;
        }
        
        // 提取患者ID列表
        List<Integer> patientIds = new ArrayList<>();
        for (DoctorPatient dp : doctorPatients) {
            patientIds.add(dp.getPid());
        }
        
        // 根据患者ID列表查询患者信息，并按序号升序排列，只取第一个
        if (patientIds.isEmpty()) {
            return null;
        }
        
        LambdaQueryWrapper<Patient> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.in(Patient::getNum, patientIds)
                      .orderByAsc(Patient::getNum)
                      .last("LIMIT 1");
        return patientMapper.selectOne(patientWrapper);
    }
    
    @Override
    public boolean removeDoctorPatientRelation(int doctorId, int patientId) {
        // 删除医生与患者的关联关系
        LambdaQueryWrapper<DoctorPatient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorPatient::getDocId, doctorId)
               .eq(DoctorPatient::getPid, patientId);
        
        int deleteResult = doctorPatientMapper.delete(wrapper);
        return deleteResult > 0;
    }
    
    @Override
    public Doctor login(int id, String username) {
        // 根据医生ID查询医生信息
        Doctor doctor = getByID(id);
        
        // 验证医生是否存在以及用户名是否匹配
        if (doctor != null && username != null && username.equals(doctor.getName())) {
            return doctor;
        }
        
        // 验证失败返回null
        return null;
    }
}