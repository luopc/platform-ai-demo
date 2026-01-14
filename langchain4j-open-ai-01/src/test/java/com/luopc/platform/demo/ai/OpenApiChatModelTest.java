package com.luopc.platform.demo.ai;

import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;

import javax.net.ssl.SSLContext;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static java.time.Duration.ofSeconds;

/**
 * Hello world!
 *
 */
public class OpenApiChatModelTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
//                .authenticator(new Authenticator() {
//                    @Override
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication("postman", "password".toCharArray());
//                    }
//                })
                .sslContext(SSLContext.getDefault());
        JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder);
        OpenAiChatModel model = OpenAiChatModel.builder()
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl("https://api.deepseek.com")
                .apiKey(System.getenv("deepseek-token"))
                .modelName("deepseek-chat")
                .logRequests(true)
                .user("ts")
                .customHeaders(Map.of("X-Custom-Header", "value", "Token", "token 1234"))
                .build();
        String answer = model.chat("How to install jDK in windows?");
        System.out.println(answer); // Hello World
    }


    public static OpenAiChatModel qwenPlusOChatModel() {
        JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(HttpClient.newBuilder());
        return OpenAiChatModel.builder()
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(System.getenv("AI_DASHSCOPE_API_KEY"))
                .modelName("qwen-plus-2025-07-28")
//                .sendThinking( true)
//                .customParameters(Map.of("enable_thinking", "true"))
                .logRequests(true).build();
    }


    public static OpenAiChatModel deepseekChatModel() {
        JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(HttpClient.newBuilder());
        return OpenAiChatModel.builder()
                .httpClientBuilder(jdkHttpClientBuilder)
                .baseUrl("https://api.deepseek.com")
                .apiKey(System.getenv("deepseek-token"))
                .modelName("deepseek-chat")
//                .temperature(0.3)
//                .timeout(ofSeconds(60))
//                .sendThinking( true)
//                .customParameters(Map.of("enable_thinking", "true"))
                .logRequests(true).build();
    }

}
