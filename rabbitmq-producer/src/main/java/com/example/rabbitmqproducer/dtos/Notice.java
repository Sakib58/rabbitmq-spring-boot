package com.example.rabbitmqproducer.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
    private int instructorId;
    private String noticeType;
    private String noticeTitle;
    private String noticeBody;
}
