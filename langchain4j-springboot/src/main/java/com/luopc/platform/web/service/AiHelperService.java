package com.luopc.platform.web.service;

import com.luopc.platform.web.entity.Recipe;
import com.luopc.platform.web.guard.SafeInputGuardrail;
import dev.langchain4j.service.*;
import dev.langchain4j.service.guardrail.InputGuardrails;
import reactor.core.publisher.Flux;

import java.util.List;

@InputGuardrails({SafeInputGuardrail.class})
public interface  AiHelperService {

    @SystemMessage(fromResource = "prompts/system-prompt.txt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "prompts/system-prompt.txt")
    Report chatForReport(String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList) {
    }

    @SystemMessage(fromResource = "prompts/system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

    @SystemMessage("You are a polite assistant")
    String chatWithHistory(@MemoryId String conversationId, @UserMessage String userMessage);

    // 新增一个使用模板的方法
    @UserMessage("""
        请创建一个 {{dish_type}} 菜肴的食谱。 
        主要食材是：{{ingredients}}。 
        请提供包含标题、简要描述、所需食材列表以及逐步操作说明的完整食谱。
        """)
    Recipe createRecipeAsObject(@V("dish_type") String dish_type, @V("ingredients") String ingredients);

    @SystemMessage("你是一名资深Java架构师，回答尽量简洁专业")
    String chatWithRole(String userMessage);

    @SystemMessage("你是一名AI助手，请逐字流式输出回答")
    Flux<String> streamChat(String userMessage);

    @SystemMessage("你是一名AI助手，根据上下文进行回答")
    String chatWithContext(@V("systemPrompt") String systemPrompt, @UserMessage String userMessage);

    @SystemMessage("你是一名AI助手，请根据上下文逐字流式输出回答")
    Flux<String> streamChatWithContext(@V("systemPrompt") String systemPrompt, @UserMessage String userMessage);

    // 流式对话
    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage);
}
