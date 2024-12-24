package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class ScoreTableDto {
    private Integer scoreTableId;
    private Integer numCorrectAnswers;
    private Integer score;
    private Integer type;
}
