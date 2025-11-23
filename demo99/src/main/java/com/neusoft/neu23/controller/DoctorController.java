package com.neusoft.neu23.controller;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.LoginRequest;
import com.neusoft.neu23.entity.Patient;
import com.neusoft.neu23.service.DoctorService;
import com.neusoft.neu23.tc.DoctorTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neusoft.neu23.cfg.AiConfig.MED_HELPER_PROMPT;
@RestController
@RequestMapping("/doctor")
@CrossOrigin
public class DoctorController {
    private final ChatClient chatClient;
    
    @Autowired
    private DoctorService doctorService;
    
    public DoctorController(OpenAiChatModel openAiChatModel, ChatMemory chatmemory,
                            DoctorTools doctorTools){
        this.chatClient =ChatClient.builder(openAiChatModel)
                .defaultSystem(MED_HELPER_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatmemory).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(doctorTools)
                .build();
    }

    @GetMapping("/helper")
    public String helper(@RequestParam(value = "msg",defaultValue = "请告诉我当前病人的病情简述") String msg,
                         @RequestParam(value = "chatId" ,defaultValue = "helper000") String chatId
    ){
        return this.chatClient.prompt()
                .user(msg)  // 提交给大模型的问题
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
    

    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        // 验证输入参数
        if (loginRequest.getId() == null || loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名和ID不能为空");
            return response;
        }
        
        try {
            // 调用service层的login方法进行验证
            Doctor doctor = doctorService.login(loginRequest.getId(), loginRequest.getUsername());
            
            if (doctor != null) {
                // 登录成功
                response.put("success", true);
                response.put("message", "登录成功");
                
                // 构建data对象，包含医生ID和姓名
                Map<String, Object> data = new HashMap<>();
                data.put("doctorId", doctor.getId());
                data.put("doctorName", doctor.getName());
                response.put("data", data);
            } else {
                // 登录失败：用户名与ID不匹配
                response.put("success", false);
                response.put("message", "用户名与ID不匹配，请检查输入");
            }
        } catch (Exception e) {
            // 处理异常情况
            response.put("success", false);
            response.put("message", "登录过程中发生错误：" + e.getMessage());
        }
        
        return response;
    }
    @GetMapping("/patientList")
    public List<Patient> getPatientsByDoctorId(@RequestParam("doctorId") int doctorId){
        return doctorService.getPatientsByDoctorId(doctorId);
    }
}
