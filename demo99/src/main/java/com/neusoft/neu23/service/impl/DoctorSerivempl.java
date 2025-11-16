package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.mapper.DoctorMapper;
import com.neusoft.neu23.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
public class DoctorSerivempl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
    @Override
    public Doctor getByID(int id) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getId, id);
        return baseMapper.selectOne(wrapper);
    }
}
