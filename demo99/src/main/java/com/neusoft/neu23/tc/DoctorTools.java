package com.neusoft.neu23.tc;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.service.DoctorService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
