package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class NoteDto {
    private Integer noteId;
    private Long userId;
    private String title;
    private String content;
}

