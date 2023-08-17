package com.example.rabbitmqproducer.services;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMqUtils {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageConverter converter;

    public void sendMessageToTopicExchange(String exchangeName, String routingKey, Object messageObject) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey , messageObject);
    }

    public void sendMessageToFanoutExchange(String exchangeName, Object messageObject) {
        rabbitTemplate.convertAndSend(exchangeName, "" , messageObject);
    }

    public void sendMessageToDirectExchange(String exchangeName, String routingKey, Object messageObject) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey , messageObject);
    }

    public void sendMessageToHeadersExchange(String exchangeName, Map<String, Object> headers, Object messageObject) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeaders(headers);
        Message message = converter.toMessage(messageObject, messageProperties);
        rabbitTemplate.convertAndSend(exchangeName, "" , message);
    }

}
