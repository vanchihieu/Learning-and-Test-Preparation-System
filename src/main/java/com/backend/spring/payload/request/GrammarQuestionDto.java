package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class GrammarQuestionDto {
    private Integer questionId;
    private Integer grammarId;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String questionExplanation;
    private Integer questionStatus = 1;
}
