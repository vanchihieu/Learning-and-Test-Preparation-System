package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class UserVocabularyDto {
    private Long userExamId;
    private Long userId; // Thay thế Integer bằng Long để đại diện
    private Integer vocabularyId; // Thay thế Integer bằng Long để đại diện
}

