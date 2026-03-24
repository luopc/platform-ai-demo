package com.luopc.platform.web.controller;


import com.luopc.platform.web.service.AgentAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentAssistant agentAssistant;

    @GetMapping("/chat")
    public String chat(@RequestParam("query") String query) {
        return agentAssistant.chat(query);
    }
}
