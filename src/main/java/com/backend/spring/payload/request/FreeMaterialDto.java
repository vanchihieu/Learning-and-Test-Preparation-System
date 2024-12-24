package com.backend.spring.payload.request;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FreeMaterialDto {
    private Integer materialId;
    private String title;
    private String description;
    private MultipartFile filePdf;
    private Integer materialStatus = 1;
}
