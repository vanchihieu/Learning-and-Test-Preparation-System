package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class GrammarContentDto {
    private Integer contentId;
    private Integer grammarId;
    private String title;
    private String content;
    private Integer grammarContentStatus = 1;
}
