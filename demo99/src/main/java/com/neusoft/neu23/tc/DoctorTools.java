package com.neusoft.neu23.tc;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.service.DoctorService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorTools {
    @Autowired
    private DoctorService doctorService;

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
}
