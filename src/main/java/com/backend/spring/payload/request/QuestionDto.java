package com.backend.spring.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuestionDto {
    private Integer questionId;
    private Integer sectionId;
    private Integer groupId;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String questionType;
    private MultipartFile questionImage;
    private String questionScript;
    private String questionExplanation;
    private MultipartFile questionAudio;
    private String questionPassage;
    private String questionText;
    private String suggestedAnswer;
    private Integer questionStatus = 1;
}
