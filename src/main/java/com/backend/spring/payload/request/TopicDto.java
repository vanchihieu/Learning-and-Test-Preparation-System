package com.backend.spring.payload.request;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TopicDto {
    private Integer topicId;
    private String topicName;
    private MultipartFile image;
    private Integer topicStatus = 1;
}


