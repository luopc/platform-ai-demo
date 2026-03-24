package com.luopc.platform.web.controller;


import com.luopc.platform.web.service.AiHelperService;
import com.luopc.platform.web.service.CacheChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import reactor.core.publisher.Flux;

/**
 * @author dev小筑
 * @copyright: 2020-2026 dev小筑
 * @className CacheChatController
 * @dateTime 2026-02-13 18:00:00
 * @description 带缓存的聊天控制器，提供会话管理和缓存聊天接口
 */
@RestController
@RequestMapping("/cache-chat")
public class CacheChatController {

    @Autowired
    private CacheChatService cacheChatService;

    @Autowired
    private AiHelperService assistant;

    /**
     * 创建新的聊天会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<Map<String, Object>> createSession() {
        try {
            String sessionId = cacheChatService.createSession();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("message", "会话创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "创建会话失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 发送消息并获取回复（带缓存）
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String userMessage = request.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "消息内容不能为空");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            String reply = cacheChatService.addMessageAndGetReply(sessionId, userMessage, assistant);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reply", reply);
            response.put("sessionId", sessionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "处理消息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取会话的所有消息历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<Map<String, Object>> getSessionMessages(@PathVariable String sessionId) {
        try {
            List<CacheChatService.ChatMessage> messages = cacheChatService.getSessionMessages(sessionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("messages", messages);
            response.put("messageCount", messages.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取消息历史失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取所有活跃会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getAllSessions() {
        try {
            List<Map<String, Object>> sessions = cacheChatService.getAllSessions();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessions", sessions);
            response.put("sessionCount", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取会话列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 删除指定会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteSession(@PathVariable String sessionId) {
        try {
            boolean deleted = cacheChatService.deleteSession(sessionId);
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "会话删除成功");
            } else {
                response.put("success", false);
                response.put("message", "会话不存在");
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除会话失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 重命名会话
     */
    @PutMapping("/sessions/{sessionId}/rename")
    public ResponseEntity<Map<String, Object>> renameSession(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        String newName = request.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "会话名称不能为空");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            boolean renamed = cacheChatService.renameSession(sessionId, newName);
            Map<String, Object> response = new HashMap<>();
            if (renamed) {
                response.put("success", true);
                response.put("message", "会话重命名成功");
            } else {
                response.put("success", false);
                response.put("message", "会话不存在");
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "重命名会话失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        try {
            Map<String, Object> stats = cacheChatService.getCacheStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 流式发送消息并获取回复（真正的流式响应）
     */
    @GetMapping(value = "/sessions/{sessionId}/stream-messages", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamMessage(
            @PathVariable String sessionId,
            @RequestParam String message) {

        return Flux.create(sink -> {
            try {
                // 首先添加用户消息到会话（不等待AI回复）
                cacheChatService.addUserMessageOnly(sessionId, message);

                // 获取会话上下文
                String context = cacheChatService.buildContextForSession(sessionId, message);

                StringBuilder fullReply = new StringBuilder();

                // 使用流式模型获取回复
                Flux<String> streamReply = assistant.streamChatWithContext(
                        "你是一个AI助手，能够根据对话历史进行连贯的回答",
                        context
                );

                // 将流式响应转发给客户端
                streamReply.subscribe(
                        data -> {
                            if (data != null && !data.isEmpty()) {
                                sink.next(data);
                                fullReply.append(data);
                            }
                        },
                        error -> {
                            sink.error(error);
                        },
                        () -> {
                            sink.next("[DONE]");
                            sink.complete();
                            // 流式传输完成后，保存AI回复到会话
                            try {
                                cacheChatService.addAssistantMessageOnly(sessionId, fullReply.toString());
                            } catch (Exception e) {
                                System.err.println("保存AI回复失败: " + e.getMessage());
                            }
                        }
                );
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    /**
     * 保存消息到会话（用于流式传输完成后）
     */
    @PostMapping("/sessions/{sessionId}/save-messages")
    public ResponseEntity<Map<String, Object>> saveMessages(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {

        try {
            String userMessage = request.get("userMessage");
            String aiReply = request.get("aiReply");

            if (userMessage == null || aiReply == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "消息内容不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 这里可以添加额外的保存逻辑
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消息保存成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "保存消息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Cache Chat Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
