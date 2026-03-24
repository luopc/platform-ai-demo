package com.luopc.platform.demo.ai.jdk.config;

import com.luopc.platform.demo.ai.jdk.resource.McpServiceResource;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

    /**
       * 注册 MCP 工具：让 Spring AI 能识别并调用
       */
    @Bean
    public ToolCallbackProvider registerTools(McpServiceResource mcpServiceResource) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpServiceResource).build();
    }
}
