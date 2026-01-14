package com.luopc.platform.web.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import java.util.Map;


@Configuration
public class ChatWithMemoryClientConfig {


    public static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    @Bean
    public MessageWindowChatMemory inMemoryMessageRepository() {
        InMemoryChatMemoryRepository inMemoryChatMemoryRepository = new InMemoryChatMemoryRepository();
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(inMemoryChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor(new LoggingRequestInterceptor()) // 注入日志拦截器
                .build();
    }

    @Bean("chatClientWithMemory")
    public ChatClient chatClientWithMemory(@Qualifier("openAiChatModel") ChatModel chatModel, MessageWindowChatMemory messageWindowChatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory).build(),
                        new SimpleLoggerAdvisor()
                ).defaultUser("tc")
                .build();
    }

    @Bean("openAiChatModel")
    public ChatModel deepseekChatModel(ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry) {
        ApiKey customApiKey = () -> System.getenv("deepseek-token");
        var openAiApi = OpenAiApi.builder()
                .apiKey(customApiKey)
                .baseUrl("https://api.deepseek.com")
                .build();
        //spring.ai.openai.base-url=https://:8000/v1
        //spring.ai.openai.chat.options.model=meta-llama/Llama-3-70B-Instruct
        //spring.ai.openai.chat.options.extra-body.top_k=40
        //spring.ai.openai.chat.options.extra-body.top_p=0.95
        //spring.ai.openai.chat.options.extra-body.repetition_penalty=1.05
        //spring.ai.openai.chat.options.extra-body.min_p=0.05
        var openAiChatOptions = OpenAiChatOptions.builder()
                .model("deepseek-chat")
                .httpHeaders(Map.of("X-Custom-Header", "value", "Token", "token 1234"))
                .extraBody(Map.of("top_k", "50"))
                .temperature(0.4);
        return new OpenAiChatModel(openAiApi, openAiChatOptions.build(), toolCallingManager, retryTemplate, observationRegistry);
    }

}
