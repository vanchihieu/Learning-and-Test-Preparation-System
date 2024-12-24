package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class LessonDto {
    private Integer lessonId;
    private String lessonName;
    private Integer lessonStatus = 1;
    private Integer sectionId;
}
