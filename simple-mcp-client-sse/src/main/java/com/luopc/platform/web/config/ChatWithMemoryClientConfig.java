package com.luopc.platform.web.config;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatWithMemoryClientConfig {

    public static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    @Bean("chatWithTools")
    public ChatClient chatClientWithMemory(ChatModel chatModel, ToolCallbackProvider tools) {
        return ChatClient.builder(chatModel)
                .defaultSystem(DEFAULT_PROMPT)
                .defaultToolCallbacks(tools.getToolCallbacks())
                .build();
    }

}
