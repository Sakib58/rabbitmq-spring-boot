package com.example.rabbitmqconsumer.services;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RabbitMqUtils {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private SimpleMessageListenerContainer simpleMessageListenerContainer;
    public Object receiveSingleMessage(String queueName) {
        return rabbitTemplate.receiveAndConvert(queueName);
    }

    public List<Object> receiveAllMessage(String queueName) {
        List<Object> allMessages = new ArrayList<>();
        Integer messageCount = amqpAdmin.getQueueInfo(queueName).getMessageCount();
        for (int i = 0; i < messageCount; i++) {
            allMessages.add(rabbitTemplate.receiveAndConvert(queueName));
        }
        return allMessages;
    }

    public Integer getMessageCount(String queueName) {
        return amqpAdmin.getQueueInfo(queueName).getMessageCount();
    }

    public void createQueue(String queueName) {
        amqpAdmin.declareQueue(new Queue(queueName));
    }

    public void deleteQueue(String queueName) {
        amqpAdmin.deleteQueue(queueName);
    }

    public void bindQueueToDirectExchange(String exchangeName, String queueName, String bindingKey) {
        amqpAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, bindingKey, null));
    }

    public void bindQueueToTopicExchange(String exchangeName, String queueName, String routingPattern) {
        amqpAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingPattern, null));
    }

    public void bindQueueToFanoutExchange(String exchangeName, String queueName) {
        amqpAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, "", null));
    }

    public void bindQueueToHeaderExchange(String exchangeName, String queueName, Map<String, Object> header) {
        amqpAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue(queueName))
                        .to(new HeadersExchange(exchangeName))
                        .whereAll(header)
                        .match()
        );
    }

    public void unbindQueueFromDirectExchange(String exchangeName, String queueName, String routingKey) {
        amqpAdmin.removeBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null));
    }

    public void unbindQueueFromTopicExchange(String exchangeName, String queueName, String routingPattern) {
        amqpAdmin.removeBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingPattern, null));
    }

    public void unbindQueueFromFanoutExchange(String exchangeName, String queueName) {
        amqpAdmin.removeBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, "", null));
    }

    public void unbindQueueFromHeaderExchange(String exchangeName, String queueName, Map<String, Object> header) {
        amqpAdmin.removeBinding(
                BindingBuilder
                        .bind(new Queue(queueName))
                        .to(new HeadersExchange(exchangeName))
                        .whereAll(header)
                        .match()
        );
    }

    public void registerListener(String queueName) {
        String[] queueNames = simpleMessageListenerContainer.getQueueNames();
        String[] newQueueNames = new String[queueNames.length+1];
        for (int i = 0; i < queueNames.length; i++) {
            newQueueNames[i] = queueNames[i];
        }
        newQueueNames[queueNames.length] = queueName;
        simpleMessageListenerContainer.setQueueNames(newQueueNames);
        simpleMessageListenerContainer.start();
    }

    public void unregisterListener(String queueName) {
        String[] queueNames = simpleMessageListenerContainer.getQueueNames();
        String[] newQueueNames = new String[queueNames.length-1];
        int j = 0;
        for (int i = 0; i < queueNames.length; i++) {
            if (queueNames[i].equals(queueName))
                continue;
            newQueueNames[j] = queueNames[i];
            j++;
        }
        simpleMessageListenerContainer.setQueueNames(newQueueNames);
        simpleMessageListenerContainer.start();
    }

    public void sendMessageWithHeaderA(String message) {
        rabbitTemplate.convertAndSend(
                "header-exchange",
                "",
                message,
                msg -> {
                    msg.getMessageProperties().getHeaders().put("queue-type", "A");
                    return msg;
                }
        );
    }

}
