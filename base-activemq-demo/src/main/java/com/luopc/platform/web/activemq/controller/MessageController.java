package com.luopc.platform.web.activemq.controller;


import com.luopc.platform.web.activemq.model.ChatMessage;
import com.luopc.platform.web.activemq.model.TestMessage;
import com.luopc.platform.web.activemq.service.MessageProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
@Tag(name = "MessageController", description = "Message endpoint")
public class MessageController {

    private final MessageProducerService messageProducerService;

    @Operation(summary = "send msg to topic", description = "test for sending msg to topic")
    @Parameters({
            @Parameter(name = "TestMessage", description = "TestMessage")
    })
    @PostMapping("/topic/send")
    public ResponseEntity<Map<String, Object>> sendToTopic(@RequestBody TestMessage request) {
        String sender = StringUtils.firstNonBlank(request.getSender(), "Anonymous");
        String content = request.getContent();

        if (content == null || content.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "消息内容不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        messageProducerService.sendToTopic(message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "消息已发送到 Topic");
        response.put("data", message);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "send msg to queue", description = "test for sending msg to queue")
    @Parameters({
            @Parameter(name = "TestMessage", description = "TestMessage")
    })
    @PostMapping("/queue/send")
    public ResponseEntity<Map<String, Object>> sendToQueue(@RequestBody TestMessage request) {
        String sender = StringUtils.firstNonBlank(request.getSender(), "Anonymous");
        String content = request.getContent();

        if (content == null || content.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "消息内容不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        messageProducerService.sendToQueue(message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "消息已发送到 Queue");
        response.put("data", message);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ActiveMQ Demo");
        return ResponseEntity.ok(response);
    }
}
