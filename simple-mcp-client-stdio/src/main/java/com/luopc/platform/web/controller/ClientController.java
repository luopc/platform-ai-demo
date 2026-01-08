package com.luopc.platform.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ClientController {

    @Resource(name = "chatWithTools")
    private ChatClient chatClient;

    @Operation(summary = "call with MCP tools", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "jdkVersion", description = "Java version")
    })
    @GetMapping("/tool/chat")
    public String chat(@RequestParam(value = "jdkVersion", defaultValue = "JDK 25的下载地址是？") String jdkVersion) {
        log.info("收到消息: {}", jdkVersion);
        String response = chatClient.prompt()
                .user(jdkVersion)
                .call()
                .content();
        log.info("响应结果: {}", response);
        return response;
    }

}
