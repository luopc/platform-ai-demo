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
            @Parameter(name = "msg", description = "msg")
    })
    @GetMapping("/tool/chat")
    public String chat(@RequestParam(value = "msg", defaultValue = "JDK 25的下载地址是？") String msg) {
        log.info("收到消息: {}", msg);
        String response = chatClient.prompt()
                .user(msg)
                .call()
                .content();
        log.info("响应结果: {}", response);
        return response;
    }

}
