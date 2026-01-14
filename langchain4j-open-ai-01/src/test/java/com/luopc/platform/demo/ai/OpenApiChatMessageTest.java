package com.luopc.platform.demo.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.Base64;

import static cn.hutool.core.io.FileUtil.readBytes;

public class OpenApiChatMessageTest {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenApiChatModelTest.qwenPlusOChatModel();

//        UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");
//        AiMessage firstAiMessage = model.chat(firstUserMessage).aiMessage(); // Hi Klaus, how can I help you?
//
//        UserMessage secondUserMessage = UserMessage.from("What is my name?");
//        AiMessage secondAiMessage = model.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); // Klaus
//        System.out.println(secondAiMessage);

        /**
         * UserMessage 不仅可以包含文本，还可以包含其他类型的内容。 UserMessage 包含 List<Content> contents。 Content 是一个接口，有以下实现：

         * TextContent
         * ImageContent
         * AudioContent
         * VideoContent
         * PdfFileContent
         */
//        ImageContent imageContent = ImageContent.from("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        byte[] imageBytes = readBytes("C:\\Users\\cheng\\OneDrive\\图片\\宋玉.jpg");
        String base64Data = Base64.getEncoder().encodeToString(imageBytes);
        ImageContent imageContent = ImageContent.from(base64Data, "image/jpg");

        UserMessage userMessage = UserMessage.from(
                TextContent.from("Describe the following image"),
                imageContent
        );
        AiMessage userContent = model.chat(userMessage).aiMessage();
        System.out.println(userContent);
    }
}
