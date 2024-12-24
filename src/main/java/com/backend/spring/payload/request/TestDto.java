package com.backend.spring.payload.request;

import lombok.Data;

import java.util.Set;

@Data
public class TestDto {
    private Integer testId;
    private String testName;// Default initial value of testProgress is set to 0
    private Integer testParticipants = 0; // Default initial value of testParticipants is set to 0
    private Integer testStatus = 1;
    private Integer sectionId;
}
