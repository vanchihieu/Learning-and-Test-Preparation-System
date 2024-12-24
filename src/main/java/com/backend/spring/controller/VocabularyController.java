package com.backend.spring.controller;

import com.backend.spring.entity.Vocabulary;
import com.backend.spring.payload.request.VocabularyDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.VocabularyService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabularies() {
        List<Vocabulary> vocabularyList = vocabularyService.getAllVocabularies();
        return new ResponseEntity<>(vocabularyList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vocabulary> getVocabularyById(@PathVariable Integer id) {
        Vocabulary vocabulary = vocabularyService.getVocabularyById(id);
        if (vocabulary != null) {
            return new ResponseEntity<>(vocabulary, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy danh sách từ vựng theo topic_id (ADMIN)
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<List<Vocabulary>> getVocabulariesByTopicId(@PathVariable Integer topicId) {
        List<Vocabulary> vocabularyList = vocabularyService.getVocabulariesByTopicId(topicId);
        if (vocabularyList != null && !vocabularyList.isEmpty()) {
            return new ResponseEntity<>(vocabularyList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-topic/{topicId}/enable")
    public ResponseEntity<List<Vocabulary>> getEnableVocabulariesByTopicId(@PathVariable Integer topicId) {
        List<Vocabulary> vocabularyList = vocabularyService.getVocabulariesByTopicId(topicId);

        // Lọc danh sách chỉ giữ lại các Vocabulary có vocabularyStatus là 1
        List<Vocabulary> filteredVocabularies = vocabularyList.stream()
                .filter(vocabulary -> vocabulary.getVocabularyStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredVocabularies.isEmpty()) {
            return new ResponseEntity<>(filteredVocabularies, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createVocabulary(@ModelAttribute VocabularyDto vocabularyDto) {
        try {
            Vocabulary createdVocabulary = vocabularyService.createVocabulary(vocabularyDto);
            return ResponseEntity.ok(new MessageResponse("Thêm từ vựng thành công!"));
        } catch (IOException | java.io.IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateVocabulary(@PathVariable Integer id, @ModelAttribute VocabularyDto vocabularyDto) {
        try {
            Vocabulary updatedVocabulary = vocabularyService.updateVocabulary(id, vocabularyDto);
            if (updatedVocabulary != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật từ vựng thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException | java.io.IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật từ vựng: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteVocabulary(@PathVariable Integer id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.ok(new MessageResponse("Xóa từ vựng thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateVocabularyStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Vocabulary vocabulary = vocabularyService.getVocabularyById(id);
            if (vocabulary != null) {
                vocabulary.setVocabularyStatus(newStatus);
                vocabularyService.updateVocabularyStatus(vocabulary);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái từ vựng thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() {
        // Tên file mẫu
        String filename = "vocabulary_template.xlsx";

        // Đọc file mẫu từ thư mục tài liệu tĩnh
        Resource resource = new ClassPathResource("/static/export-template/" + filename);

        // Cài đặt tiêu đề và loại dữ liệu trả về
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Trả về file mẫu dưới dạng tệp tin (Resource)
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadVocabularyFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topicId") Integer topicId) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn 1 file để upload"), HttpStatus.BAD_REQUEST);
        }

        try {
            vocabularyService.uploadVocabularyFromExcel(file, topicId);
            return ResponseEntity.ok(new MessageResponse("Upload thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Upload thất bại: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
