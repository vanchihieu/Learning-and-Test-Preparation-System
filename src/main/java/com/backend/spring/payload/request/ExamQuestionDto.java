package com.backend.spring.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ExamQuestionDto {
    private Integer examQuestionId; // Tùy theo nhu cầu
    private Integer examId; // Tùy theo nhu cầu
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
    private Integer questionStatus = 1;
    private String questionPassage; // Thêm trường này nếu cần
    private Integer orderNumber; // Thêm trường này nếu cần
    private Integer questionPart; // Tùy theo nhu cầu
}
