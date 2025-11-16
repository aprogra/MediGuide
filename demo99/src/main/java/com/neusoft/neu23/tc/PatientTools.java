package com.neusoft.neu23.tc;

import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.service.PatientService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientTools {
    @Autowired
    private PatientService patientService;

    @Tool(description = "将病人的信息保存到数据库，并返回序列号，但必须收集病人的姓名与病情描述")
    public String savePatientInfo(
            @ToolParam(description = "病人的姓名，参数name表示病人的姓名")String name,
            @ToolParam(description = "病人的病情描述，参数descriptions表示病人的病情描述")String descriptions){
        Integer pid = patientService.addPatient(new Patient(null,name,descriptions));
        return pid+"";
    }
}
