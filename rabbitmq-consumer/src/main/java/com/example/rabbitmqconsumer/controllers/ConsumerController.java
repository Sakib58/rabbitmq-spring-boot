package com.example.rabbitmqconsumer.controllers;

import com.example.rabbitmqconsumer.dtos.Student;
import com.example.rabbitmqconsumer.dtos.SubscriptionMap;
import com.example.rabbitmqconsumer.services.RabbitMqUtils;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class ConsumerController {

    @Autowired
    private RabbitMqUtils rabbitMqUtils;

    @Autowired
    private DirectExchange directExchange;

    @Autowired
    private TopicExchange topicExchange;

    @Autowired
    private FanoutExchange fanoutExchange;

    @Autowired
    private HeadersExchange headersExchange;

    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        String queueName = "student." + student.getId();
        String bindingKey = queueName;
        rabbitMqUtils.createQueue(queueName);
        rabbitMqUtils.bindQueueToFanoutExchange(fanoutExchange.getName(), queueName);
        rabbitMqUtils.bindQueueToDirectExchange(directExchange.getName(),queueName, bindingKey);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeInstructor(@RequestBody SubscriptionMap subscriptionMap) {
        String queueName = "student." + subscriptionMap.getStudentId();
        /**The routing patterns may include an asterisk (“*”) to match a word
        in a specified position of the routing key (for example, a routing pattern
        of “agreements.*.*.b.*” only matches routing keys with “agreements”
        as the first word and “b” as the fourth word). A pound symbol (“#”) denotes
        a match of zero or more words.*/
        String bindingKey = "instructor."+subscriptionMap.getInstructorId()+".#";
        rabbitMqUtils.bindQueueToTopicExchange(topicExchange.getName(),queueName,bindingKey);
        return ResponseEntity.ok(subscriptionMap);
    }

    @PostMapping("/un-subscribe")
    public ResponseEntity<?> unSubscribeInstructor(@RequestBody SubscriptionMap subscriptionMap) {
        String queueName = "student." + subscriptionMap.getStudentId();
        String routingKey = "instructor."+subscriptionMap.getInstructorId()+".#";
        rabbitMqUtils.unbindQueueFromTopicExchange(topicExchange.getName(),queueName, routingKey);
        return ResponseEntity.ok("Unsubscribe successful!");
    }

    @GetMapping("/receive-notice/{id}")
    public ResponseEntity<String> receiveMessageFromQueue(@PathVariable Long id) {
        String queueName = "student." +id;
        Object messageObject = rabbitMqUtils.receiveSingleMessage(queueName);
        if (messageObject != null) {
            return ResponseEntity.ok("Received message: " + messageObject.toString());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/receive-all-notices/{id}")
    public ResponseEntity<String> receiveAllMessagesFromQueue(@PathVariable Long id) {
        String queueName = "student."+id;
        List<Object> allMessages = rabbitMqUtils.receiveAllMessage(queueName);
        if (allMessages != null) {
            return ResponseEntity.ok("Received Messages:" + allMessages.toString());
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/get-message-count/{id}")
    public ResponseEntity<String> getMessageCount(@PathVariable Long id) {
        String queueName = "student." + id;
        Integer messageCount = rabbitMqUtils.getMessageCount(queueName);
        if (messageCount != null) {
            return ResponseEntity.ok("Total message count: " + messageCount);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Student student) {
        rabbitMqUtils.registerListener("student."+student.getId());
        return ResponseEntity.ok("Login Successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Student student) {
        rabbitMqUtils.unregisterListener("student."+student.getId());
        return ResponseEntity.ok("Logout Successful!");
    }

    @PostMapping("/create-secure-channel")
    public ResponseEntity<String> createSecureChannel(@RequestBody Student student, @RequestParam Map<String, Object> headers) {
        rabbitMqUtils.bindQueueToHeaderExchange(headersExchange.getName(), "student."+student.getId(),headers);
        return ResponseEntity.ok("Secure channel created!");
    }
}
