package com.backend.spring.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SectionDto {
    private Integer id;
    private String name;
    private String description;
    private Integer status = 1;
    private MultipartFile image;
    private Integer type;
}

