package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class ExamDto {
    private Integer examId;
    private String examName;
    private Integer examType;// Thời gian duy trì (số giờ hoặc số phút)
    private Integer examStatus = 1;
}
