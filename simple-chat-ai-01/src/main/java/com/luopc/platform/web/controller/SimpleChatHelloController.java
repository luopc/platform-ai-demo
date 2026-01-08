package com.luopc.platform.web.controller;

import com.luopc.platform.web.common.core.util.SimpleJsonUtil;
import com.luopc.platform.web.common.core.util.SimpleNumIDUtil;
import com.luopc.platform.web.config.ChatWithMemoryClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/api/simple", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "SimpleChatHelloController", description = "简单的对话示例模版")
public class SimpleChatHelloController {

    private final ChatClient chatClient;

    @Resource(name = "chatClientWithMemory")
    private ChatClient memoryChatClient;
    @Resource(name = "inMemoryMessageRepository")
    private ChatMemory chatMemory;

    public SimpleChatHelloController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(ChatWithMemoryClientConfig.DEFAULT_PROMPT)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Operation(summary = "获取单条记录", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "name", description = "用户姓名")
    })
    @GetMapping("/get")
    public String getUser(@RequestParam(value = "name", defaultValue = "David") String name) {
        log.info("Received request to list users by name:{}", name);
        Map<String, Object> user = new HashMap<>();
        user.put("id", SimpleNumIDUtil.nextPkId());
        user.put("name", StringUtils.isNoneBlank(name) ? name : "Zhang san");
        return SimpleJsonUtil.writeJson(user);
    }

    @Operation(summary = "简单的AI调用-call", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "prompt", description = "user query")
    })
    @GetMapping("/simple/chat")
    public ResponseEntity<String> simpleChat(@RequestParam(value = "prompt", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String prompt) {
        return ResponseEntity.ok(chatClient.prompt().user(prompt).call().content());
    }

    @Operation(summary = "简单的AI调用-response", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "prompt", description = "user query")
    })
    @GetMapping("/simple/message")
    public AssistantMessage messageChat(@RequestParam(value = "prompt", defaultValue = "你可以讲一个笑话么？") String prompt) {
        return Objects.requireNonNull(chatClient.prompt(prompt).call().chatResponse()).getResult().getOutput();
    }

    @Operation(summary = "流式调用", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "prompt", description = "user query")
    })
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam(value = "prompt", defaultValue = "能给我讲一个故事么？") String prompt, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(prompt).stream().content();
    }

    @Operation(summary = "返回Entity", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "prompt", description = "user query")
    })
    @GetMapping("/chat/movie")
    public ResponseEntity<Object> movies(@RequestParam(value = "prompt", defaultValue = "请告诉我周润发主演的电影。") String prompt) {
        return ResponseEntity.ok(chatClient.prompt().user(prompt).call().entity(Movie.class));
    }

    record Movie(String actor, List<Film> movies) {
    }

    record Film(String name, String director) {
    }

    @Operation(summary = "Memory-call", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "conversationId", description = "会话ID"),
            @Parameter(name = "prompt", description = "提问请求")
    })
    @GetMapping("/memory/chat")
    public String memoryChat(@RequestParam(value = "conversationId", defaultValue = "10001") String conversationId,
                             @RequestParam(value = "prompt", defaultValue = "你能和我讲一个故事么？") String prompt) {
        return memoryChatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(prompt)
                .call()
                .content();
    }

    @Operation(summary = "带记忆功能的流式调用", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "conversationId", description = "会话ID"),
            @Parameter(name = "prompt", description = "提问请求")
    })
    @GetMapping("/streamMemory/chat")
    public Flux<String> streamMemoryChat(@RequestParam(value = "conversationId", defaultValue = "10001") String conversationId,
                                         @RequestParam(value = "prompt", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String prompt,
                                         HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return memoryChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(prompt)
                .stream()
                .content();
    }

    @Operation(summary = "获取对话历史记录", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "conversationId", description = "会话ID")
    })
    @GetMapping("/memory/history")
    public ResponseEntity<Object> history(@RequestParam(value = "conversationId", defaultValue = "10001") String conversationId) {
        return ResponseEntity.ok(chatMemory.get(conversationId));
    }

    @Operation(summary = "清除对话历史记录", description = "不需要登录后访问")
    @Parameters({
            @Parameter(name = "conversationId", description = "会话ID")
    })
    @DeleteMapping("/history")
    public ResponseEntity<Object> clear(@RequestParam(value = "conversationId", defaultValue = "10001") String conversationId) {
        chatMemory.clear(conversationId);
        return ResponseEntity.ok("聊天记录已清除!");
    }

}
