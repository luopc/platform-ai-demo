package com.luopc.platform.web.config;

import com.luopc.platform.web.service.ExplainerService;
import com.luopc.platform.web.tools.CodeAnalysisTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.skills.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Configuration
public class SkillConfig {


    @Bean
    public ExplainerService explainerService(ChatModel chatModel) {
        List<FileSystemSkill> skillList =
                FileSystemSkillLoader.loadSkills(Path.of("skills/"));
        Skills skills = Skills.from(skillList);

        return AiServices.builder(ExplainerService.class)
                .chatModel(chatModel)
                .tools(new CodeAnalysisTools()) //MCP Tools
                .toolProvider(skills.toolProvider())
                .systemMessage(
                        "你是一个代码分析助手。\n" +
                                "你可以使用以下技能:\n" +
                                skills.formatAvailableSkills() +
                                "\n当用户要求解释代码时,使用 activate_skill 工具激活 explain-code 技能。"
                )
                .build();
    }

    public Skill incidentSkill() {
        Skill skill = Skill.builder().name("incident-response")
                .description("生产环境故障排查与处理的逐步指南")
                .content("""
                        当线上告警触发时：
                        1. 调用 fetchRecentLogs(serviceName) 获取最近 5 分钟日志。
                        2. 调用 checkServiceHealth(serviceName) 获取当前健康指标。
                        3. 根据发现，调用 createIncidentTicket(summary, severity) 创建故障单。
                        4. 如果 severity 是 CRITICAL，再调用 pageOnCall(incidentId) 呼叫值班人员。
                        """)
                .build();
        log.info(" ------------------------ Skill initial completed -------------------------");
        return skill;
    }


    public Skill resourceSkill() {
        SkillResource reference = SkillResource.builder()
                .relativePath("references/tone-guide.md")
                .content("使用温暖、简洁的语气。避免行话。")
                .build();

        Skill skill = Skill.builder()
                .name("customer-support")
                .description("处理客户支持咨询")
                .content("遵循 references/tone-guide.md 中的语气指南 ...")
                .resources(List.of(reference))
                .build();
        log.info(" ------------------------ Skill references completed -------------------------");
        return skill;
    }

}
