package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class VocabularyQuestionDto {
    private Integer questionId;
    private Integer topicId;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String questionExplanation;
    private Integer questionStatus = 1;
}
