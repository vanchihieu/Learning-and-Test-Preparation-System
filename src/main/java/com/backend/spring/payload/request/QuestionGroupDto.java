package com.backend.spring.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuestionGroupDto {
    private Integer groupId;
    private MultipartFile groupImage;
    private String groupScript;
    private MultipartFile groupAudio;
    private String groupPassage;
    private String groupText;
}
