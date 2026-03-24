package com.luopc.platform.web.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CodeAnalysisTools {

    @Tool("Show code difference")
    public String showDiff(String code1, String code2) {
        return "";
    }
}
