package com.luopc.platform.web.controller;


import com.luopc.platform.web.entity.Recipe;
import com.luopc.platform.web.service.AiHelperService;
import com.luopc.platform.web.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

/**
 * @author dev小筑
 * @copyright: 2020-2026 dev小筑
 * @className AiServiceController
 * @dateTime 2026-02-13 16:00:00
 * @description AI服务控制器，提供聊天和流式聊天接口
 */
@RestController
@RequestMapping("/ai")
public class AiServiceController {

    @Autowired
    private AiHelperService assistant;
    @Autowired
    DocumentService documentService;

    @GetMapping("/v2/chat")
    public String chatV2(@RequestParam String msg) {
        return assistant.chatWithRole(msg);
    }

    @GetMapping(value = "/v2/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam String msg) {
        return assistant.streamChat(msg);
    }

    @GetMapping("/v2/chat-with-context")
    public String chatWithContext(@RequestParam String systemPrompt, @RequestParam String userMessage) {
        return assistant.chatWithContext(systemPrompt, userMessage);
    }

    @GetMapping(value = "/v2/stream-with-context", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamWithContext(@RequestParam String systemPrompt, @RequestParam String userMessage) {
        return assistant.streamChatWithContext(systemPrompt, userMessage);
    }


    @GetMapping("/recipe")
    public Recipe recipe(String dishType, String ingredients) {
        return assistant.createRecipeAsObject(dishType, ingredients);
    }

    @GetMapping("/loadDocument")
    public List<String> loadDocument() throws IOException {
        return documentService.loadSplitAndEmbed();
    }

    @GetMapping("/search")
    public List<String> search(String query) {
        return documentService.search(query);
    }
}
