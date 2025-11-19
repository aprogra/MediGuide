package com.neusoft.neu23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.neu23.entity.MedicineResult;

public interface MedicineResultService extends IService<MedicineResult> {
    /**
     * 保存配药结果
     * @param medicineResult 配药结果对象
     * @return 保存结果：1-成功，-1-失败
     */
    Integer saveMedicineResult(MedicineResult medicineResult);
}