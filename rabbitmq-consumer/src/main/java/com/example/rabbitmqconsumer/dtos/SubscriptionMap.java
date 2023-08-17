package com.example.rabbitmqconsumer.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionMap {
    private Long id;
    private Long studentId;
    private Long instructorId;
}
