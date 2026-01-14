package com.luopc.platform.demo.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.List;

/**
 * https://bbs.huaweicloud.com/blogs/452105
 */
public class StreamingChatLanguageModelTest {
//
//    private final ChatLanguageModel chatLanguageModel;
//
//    public SimpleStreamingChat(ChatLanguageModel chatLanguageModel) {
//        this.chatLanguageModel = chatLanguageModel;
//    }
//
//    public void chat(String userMessage) {
//        List<ChatMessage> messages = List.of(ChatMessage.user(userMessage));
//        Flux<AiMessage> responsePublisher = chatLanguageModel.generateStream(messages);
//
//        responsePublisher.subscribe(
//                aiMessage -> System.out.print(aiMessage.text()), // 打印每个文本片段
//                error -> System.err.println("Error during streaming: " + error),
//                () -> System.out.println("\n-- Stream completed --")
//        );
//
//        // 注意：这里需要阻塞主线程，否则流可能在打印完成前结束
//        try {
//            Thread.sleep(5000); // 假设流在 5 秒内完成
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    public static void main(String[] args) {
//        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
//                .baseUrl("https://api.deepseek.com")
//                .apiKey(System.getenv("deepseek-token"))
//                .modelName("deepseek-chat")
//                .build();
//
//        String userMessage = "Tell me a joke";
//
//        model.chat(userMessage, new StreamingChatResponseHandler() {
//
//            @Override
//            public void onPartialResponse(String partialResponse) {
//                System.out.println("onPartialResponse: " + partialResponse);
//            }
//
//            @Override
//            public void onCompleteResponse(ChatResponse completeResponse) {
//                System.out.println("onCompleteResponse: " + completeResponse);
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                error.printStackTrace();
//            }
//        });
//
//
//    }
}
