package com.neusoft.neu23.controller;

import com.neusoft.neu23.tc.DoctorTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import static com.neusoft.neu23.cfg.AiConfig.MED_HELPER_PROMPT;

@RestController
@RequestMapping("/doctor")
@CrossOrigin
public class DoctorController {
    private final ChatClient chatClient;
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
}
