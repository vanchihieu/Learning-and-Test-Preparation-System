package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class GrammarDto {
    private Integer grammarId;
    private String grammarName;
    private Integer grammarStatus = 1;
}
