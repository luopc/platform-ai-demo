package com.luopc.platform.demo.ai;


import com.luopc.platform.demo.ai.jdk.resource.McpServiceResource;
import com.luopc.platform.demo.ai.jdk.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpStdioServerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpStdioServerApiApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(McpServiceResource mcpServiceResource) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpServiceResource).build();
    }
}
