package com.luopc.platform.web.controller;

import com.luopc.platform.web.service.KnowledgeBaseAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kb")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseAssistant knowledgeBaseAssistant;

    @GetMapping("/chat")
    public String chat(@RequestParam("query") String query) {
        return knowledgeBaseAssistant.chat(query);
    }
}
