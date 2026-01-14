package com.luopc.platform.cloud.service.ai;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import reactor.core.publisher.Flux;

import java.util.Map;

public class OpenApiTest {
    public static void main(String[] args) {

        var openAiApi = OpenAiApi.builder()
                .apiKey(System.getenv("deepseek-token"))
                .baseUrl("https://api.deepseek.com")
                .build();

        var openAiChatOptions = OpenAiChatOptions.builder()
                .model("deepseek-chat")
                .httpHeaders(Map.of("X-Custom-Header", "value", "Token", "token 1234"))
                .temperature(0.4)
                .maxTokens(200)
                .build();
//        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);
//
//        ChatResponse response = chatModel.call(
//                new Prompt("Generate the names of 5 famous pirates."));
//
//        // Or with streaming responses
//        Flux<ChatResponse> streamResponse = chatModel.stream(
//                new Prompt("Generate the names of 5 famous pirates."));
    }
}
