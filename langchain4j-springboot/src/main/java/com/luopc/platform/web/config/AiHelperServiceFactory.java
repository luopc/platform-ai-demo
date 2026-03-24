package com.luopc.platform.web.config;

import com.luopc.platform.web.service.AgentAssistant;
import com.luopc.platform.web.service.AiHelperService;
import com.luopc.platform.web.service.KnowledgeBaseAssistant;
import com.luopc.platform.web.tools.CalculatorTools;
import com.luopc.platform.web.tools.InnerResourceTool;
import com.luopc.platform.web.tools.WeatherTools;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AiHelperServiceFactory {


    @Resource
    private ChatModel myQwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private ContentRetriever contentRetriever;

//    @Resource
//    private McpToolProvider mcpToolProvider;

    @Bean
    public AiHelperService aiHelperService() {
        // 会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造 AI Service
        AiHelperService aiHelperService = AiServices.builder(AiHelperService.class)
                .chatModel(myQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.withMaxMessages(10)) // 每个会话独立存储
                .contentRetriever(contentRetriever) // RAG 检索增强生成
                .tools(new InnerResourceTool()) // 工具调用
//                .toolProvider(mcpToolProvider) // MCP 工具调用
                .build();
        log.info(" ------------------------ AI Service initial completed -------------------------");
        return aiHelperService;
    }

//    @Bean
//    public EmbeddingModel embeddingModel() {
//        // 通常嵌入模型也使用相同的api-key和base-url
//        // 注意：OpenAI有专门的嵌入模型名称，
//        // 比如 "text-embedding-ada-002" 。
//        // 如果不指定，LangChain4j可能会使用一个默认值。
//        // 为清晰起见，最好在properties中也定义它。
//        return OpenAiEmbeddingModel.builder()
//                .apiKey(apiKey)
//                .baseUrl(baseUrl)
//                // 推荐在application.properties中添加:
//                // langchain4j.open-ai.embedding-model.model-name=text-embedding-ada-002
//                .modelName(embeddingModelName)
//                .build();
//    }


//    // 步骤1: 创建ContentRetriever Bean
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        // 这个检索器知道如何从向量存储中根据语义相似度检索内容
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3) // 每次检索最多返回3个最相关的片段
                .build();
    }

    // 步骤2: 创建RetrievalAugmentor Bean
    @Bean
    public RetrievalAugmentor retrievalAugmentor(ContentRetriever contentRetriever) {
        // 这是默认的检索增强器，它会使用提供的内容检索器
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }

//     步骤3: 创建最终的、具备RAG能力的AI服务Bean
    @Bean
    public KnowledgeBaseAssistant knowledgeBaseAssistant(ChatModel chatLanguageModel, RetrievalAugmentor retrievalAugmentor) {
        return AiServices.builder(KnowledgeBaseAssistant.class)
                .chatModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    // 修改Agent服务接口Bean
    @Bean
    public AgentAssistant agentAssistant(ChatModel chatLanguageModel,
                                         CalculatorTools calculatorTools,
                                         WeatherTools weatherTools) { // 注入新工具
        return AiServices.builder(AgentAssistant.class)
                .chatModel(chatLanguageModel)
                .tools(calculatorTools, weatherTools) // <-- 关键步骤：将所有工具都注册进去
                .build();
    }
}
