package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class UserExamQuestionDto {
    private Integer userExamQuestionId; // Tùy theo nhu cầu
    private Long userExamId; // Thay thế Integer bằng Long để đại diện
    private Integer examQuestionId; // Tùy theo nhu cầu
    private String selectedOption;
    private Integer isCorrect;
}
