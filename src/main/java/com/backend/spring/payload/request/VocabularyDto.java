package com.backend.spring.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VocabularyDto {
    private Integer vocabularyId;
    private String word;
    private String ipa;
    private String meaning;
    private String exampleSentence;
    private MultipartFile image;
    private Integer topicId;
    private Integer vocabularyStatus = 1;
}
