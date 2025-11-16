package com.neusoft.neu23.controller;

import com.neusoft.neu23.tc.PatientTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import static com.neusoft.neu23.cfg.AiConfig.TEST;

@RestController
@RequestMapping("/dc")
@CrossOrigin
public class DemoController {
    /**
     * 使用构造器注入ChatClient
     */
    private final ChatClient chatClient;
    public DemoController(OpenAiChatModel openAiChatModel, ChatMemory chatmemory,
                          PatientTools patientTools) {
        this.chatClient =ChatClient.builder(openAiChatModel)
                .defaultSystem(TEST)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatmemory).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(patientTools)
                .build();
    }

    @GetMapping("/c4")
    public String chat4(@RequestParam(value = "msg",defaultValue = "你是谁") String msg,
                        @RequestParam( value = "chatId" ,defaultValue = "neu.edu.cn") String chatId
    ){
        return this.chatClient.prompt()
                .user(msg)  // 提交给大模型的问题
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
