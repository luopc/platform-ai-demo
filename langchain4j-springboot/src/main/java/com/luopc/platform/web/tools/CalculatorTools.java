package com.luopc.platform.web.tools;


import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component // 将这个类注册为一个Spring Bean
public class CalculatorTools {

    @Tool("Calculates the sum of two integers")
    public int add(int a, int b) {
        System.out.println("Tool executed: add(" + a + ", " + b + ")");
        return a + b;
    }

    @Tool("Calculates the difference between two integers")
    public int subtract(int a, int b) {
        System.out.println("Tool executed: subtract(" + a + ", " + b + ")");
        return a - b;
    }
}
