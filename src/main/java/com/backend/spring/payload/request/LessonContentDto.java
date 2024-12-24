package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class LessonContentDto {
    private Integer contentId;
    private Integer lessonId;
    private String title;
    private String content;
    private Integer lessonContentStatus = 1;
}
