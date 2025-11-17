package com.neusoft.neu23.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.neu23.entity.ResultOfDiagnosis;
import com.neusoft.neu23.mapper.ResultOfDiagnosisMapper;
import com.neusoft.neu23.service.ResultOfDiagnosisService;
import org.springframework.stereotype.Service;

@Service
public class ResultOfDiagnosisSerivempl extends ServiceImpl<ResultOfDiagnosisMapper, ResultOfDiagnosis> implements ResultOfDiagnosisService {
    @Override
    public Integer saveDiagnosisResult(ResultOfDiagnosis resultOfDiagnosis) {
        int i = baseMapper.insert(resultOfDiagnosis);
        if (i > 0) {
            return 1; // 保存成功
        }
        return -1; // 保存失败
    }
}

