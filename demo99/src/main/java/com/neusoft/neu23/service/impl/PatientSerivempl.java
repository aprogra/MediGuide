package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.mapper.PatientMapper;
import com.neusoft.neu23.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientSerivempl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Override
    public Integer addPatient(Patient patient){
        patient.setNum(null);
        int i = baseMapper.insert(patient);
        if(i>0)
            return patient.getNum();
        return -1;
    }
}
