package com.example.rabbitmqproducer.controller;

import com.example.rabbitmqproducer.dtos.Instructor;
import com.example.rabbitmqproducer.dtos.Notice;
import com.example.rabbitmqproducer.services.RabbitMqUtils;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/instructor")
public class ProducerController {

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
    public ResponseEntity<?> addInstructor(@RequestBody Instructor instructor) {
        return ResponseEntity.ok(instructor);
    }

    @PostMapping("/notices-to-subscribers")
    public ResponseEntity<String> sendNoticeToSubscribers(@RequestBody Notice notice) {
        String routingKey = "instructor." + notice.getInstructorId() + "." + notice.getNoticeType();
        rabbitMqUtils.sendMessageToTopicExchange(topicExchange.getName(),routingKey,notice);
        return ResponseEntity.ok("Notice sent successfully");
    }

    @PostMapping("/notices-to-all-students")
    public ResponseEntity<String> sendNoticeToAllStudents(@RequestBody Notice notice) {
        rabbitMqUtils.sendMessageToFanoutExchange(fanoutExchange.getName(), notice);
        return ResponseEntity.ok("Notice sent successfully");
    }

    @PostMapping("/notices-to-particular-student/{studentId}")
    public ResponseEntity<String> sendNoticeToParticularStudents(@RequestBody Notice notice, @PathVariable Long studentId) {
        String routingKey = "student." + studentId;
        rabbitMqUtils.sendMessageToDirectExchange(directExchange.getName(), routingKey,notice);
        return ResponseEntity.ok("Notice sent successfully");
    }

    @PostMapping("/send-message-to-secure-channel")
    public ResponseEntity<String> sendNoticeToSecureChannel(@RequestBody Notice notice, @RequestParam Map<String, Object> headers) {
        rabbitMqUtils.sendMessageToHeadersExchange(headersExchange.getName(), headers, notice);
        return ResponseEntity.ok("Notice sent successfully");
    }
}
