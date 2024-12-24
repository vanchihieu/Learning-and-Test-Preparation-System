package com.backend.spring.payload.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserGoalDto {
    private Long userGoalId;
    private Long userId;
    private Integer goalScore;
}

