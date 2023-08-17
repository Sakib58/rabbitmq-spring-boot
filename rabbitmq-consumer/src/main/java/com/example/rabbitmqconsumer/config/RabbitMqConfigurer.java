package com.example.rabbitmqconsumer.config;

import com.example.rabbitmqconsumer.dtos.Notice;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfigurer {
    @Value("${rabbitmq.direct-exchange.name}")
    private String directExchangeName;

    @Value("${rabbitmq.topic-exchange.name}")
    private String topicExchangeName;

    @Value("${rabbitmq.fanout-exchange.name}")
    private String fanoutExchangeName;

    @Value("${rabbitmq.header-exchange.name}")
    private String headerExchangeName;

    private final ConnectionFactory connectionFactory;

    public RabbitMqConfigurer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(fanoutExchangeName);
    }

    @Bean
    HeadersExchange headerExchange() {
        return new HeadersExchange(headerExchangeName);
    }

    @Bean
    public AmqpTemplate template() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setMessageListener(messageListener());
        return simpleMessageListenerContainer;
    }

    @Bean
    MessageListener messageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("Received Message - " + new String(message.getBody()));
            }
        };
    }

//    @RabbitListener(queues = "student.4")
//    public void listenQueue(Notice message) {
//        System.out.println(message);
//    }

}
