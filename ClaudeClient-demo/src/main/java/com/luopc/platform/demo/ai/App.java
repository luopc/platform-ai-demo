package com.luopc.platform.demo.ai;

/**
 * <a href="https://spring-ai-community.github.io/agent-client/api/claude-code-sdk.html">Claude Agent SDK</a>
 */
public class App {
//    public static void main(String[] args) {
//        // 1. Create the Claude Code client
//        ClaudeAgentClient claudeClient = ClaudeAgentClient.create(
//                Paths.get(System.getProperty("user.dir"))
//        );
//
//        // 2. Configure agent options
//        ClaudeAgentOptions options = ClaudeAgentOptions.builder()
//                .model("claude-sonnet-4-20250514")
//                .yolo(true)
//                .build();
//
//        // 3. Create the agent model
//        ClaudeAgentModel agentModel = new ClaudeAgentModel(claudeClient, options);
//
//        // 4. Create AgentClient
//        AgentClient agentClient = AgentClient.create(agentModel);
//
//        // 5. Execute a goal
//        AgentClientResponse response = agentClient.run(
//                "Create a simple Calculator class with add, subtract, multiply, and divide methods"
//        );
//
//        System.out.println("Result: " + response.getResult());
//        System.out.println("Success: " + response.isSuccessful());
//    }
}
