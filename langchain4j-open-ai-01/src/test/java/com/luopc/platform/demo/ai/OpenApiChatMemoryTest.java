package com.luopc.platform.demo.ai;

import com.openai.models.beta.assistants.Assistant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;
import java.util.Map;

public class OpenApiChatMemoryTest {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenApiChatModelTest.deepseekChatModel();
        ChatMemory chatMemory_01 = MessageWindowChatMemory.withMaxMessages(10);


        ChatMemory chatMemory_02 = MessageWindowChatMemory.builder()
                .id("ID_12345")
                .maxMessages(10)
//                .chatMemoryStore(new PersistentChatMemoryStore())
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory_01)
                .build();

        String answer = assistant.chat("Hello! My name is Klaus.");
        System.out.println(answer); // Hello Klaus! How can I assist you today?

        String answerWithName = assistant.chat("What is my name?");
        System.out.println(answerWithName); // Your name is Klaus.
    }

    interface Assistant {
        String chat(String message);
    }


    static class PersistentChatMemoryStore implements ChatMemoryStore {

//        private final DB db = DBMaker.fileDB("multi-user-chat-memory.db").transactionEnable().make();
//        private final Map<Integer, String> map = db.hashMap("messages", INTEGER, STRING).createOrOpen();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            // TODO: 实现通过内存ID从持久化存储中获取所有消息。
            // 可以使用ChatMessageDeserializer.messageFromJson(String)和
            // ChatMessageDeserializer.messagesFromJson(String)辅助方法
            // 轻松地从JSON反序列化聊天消息。
            return null;
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            // TODO: 实现通过内存ID更新持久化存储中的所有消息。
            // 可以使用ChatMessageSerializer.messageToJson(ChatMessage)和
            // ChatMessageSerializer.messagesToJson(List<ChatMessage>)辅助方法
            // 轻松地将聊天消息序列化为JSON。
        }

        @Override
        public void deleteMessages(Object memoryId) {
            // TODO: 实现通过内存ID删除持久化存储中的所有消息。
        }
    }
}
