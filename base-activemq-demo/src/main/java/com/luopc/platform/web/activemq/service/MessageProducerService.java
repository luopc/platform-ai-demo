package com.luopc.platform.web.activemq.service;


import com.luopc.platform.web.activemq.constant.MqConstant;
import com.luopc.platform.web.activemq.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private final JmsTemplate jmsTemplate;

    public void sendToTopic(ChatMessage message) {
        log.info("发送消息到 Topic: {}, 消息内容：{}", MqConstant.CHAT_TOPIC, message);
        jmsTemplate.convertAndSend(MqConstant.CHAT_TOPIC, message);
    }

    public void sendToQueue(ChatMessage message) {
        log.info("发送消息到 Queue: {}, 消息内容：{}", MqConstant.CHAT_QUEUE, message);
        jmsTemplate.convertAndSend(MqConstant.CHAT_QUEUE, message);
    }
}
