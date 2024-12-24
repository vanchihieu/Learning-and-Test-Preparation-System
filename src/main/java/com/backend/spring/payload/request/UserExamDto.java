package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class UserExamDto {
    private Long userExamId;
    private Long userId; // Thay thế Integer bằng Long để đại diện
    private Long examId; // Thay thế Integer bằng Long để đại diện
    private Integer completionTime;
    private Integer numListeningCorrectAnswers;
    private Integer listeningScore;
    private Integer numReadingCorrectAnswers;
    private Integer readingScore;
    private Integer totalScore;
    private Integer numCorrectAnswers;
    private Integer numWrongAnswers;
    private Integer numSkippedQuestions;
    private Integer goalScore;
}

