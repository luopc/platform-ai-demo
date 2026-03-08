package com.luopc.platform.web.activemq.service;

import com.luopc.platform.web.activemq.constant.MqConstant;
import com.luopc.platform.web.activemq.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumerService {

    @JmsListener(destination = MqConstant.CHAT_TOPIC, containerFactory = "jmsListenerContainerFactory")
    public void receiveFromTopic(ChatMessage message) {
        log.info("========== 从 Topic 接收到消息 ==========");
        log.info("消息 ID: {}", message.getId());
        log.info("发送者：{}", message.getSender());
        log.info("消息内容：{}", message.getContent());
        log.info("时间戳：{}", message.getTimestamp());
        log.info("==========================================");
    }

    @JmsListener(destination = MqConstant.CHAT_QUEUE)
    public void receiveFromQueue(ChatMessage message) {
        log.info("========== 从 Queue 接收到消息 ==========");
        log.info("消息 ID: {}", message.getId());
        log.info("发送者：{}", message.getSender());
        log.info("消息内容：{}", message.getContent());
        log.info("时间戳：{}", message.getTimestamp());
        log.info("==========================================");
    }
}
