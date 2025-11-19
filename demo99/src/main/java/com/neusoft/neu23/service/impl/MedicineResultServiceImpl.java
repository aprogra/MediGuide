package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.MedicineResult;
import com.neusoft.neu23.mapper.MedicineResultMapper;
import com.neusoft.neu23.service.MedicineResultService;
import org.springframework.stereotype.Service;

@Service
public class MedicineResultServiceImpl extends ServiceImpl<MedicineResultMapper, MedicineResult> implements MedicineResultService {
    
    @Override
    public Integer saveMedicineResult(MedicineResult medicineResult) {
        int i = baseMapper.insert(medicineResult);
        if (i > 0) {
            return 1; // 保存成功
        }
        return -1; // 保存失败
    }
}