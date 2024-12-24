package com.backend.spring.controller;

import com.backend.spring.entity.VocabularyQuestion;
import com.backend.spring.payload.request.VocabularyQuestionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.VocabularyQuestionService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vocabulary-question")
public class VocabularyQuestionController {

    @Autowired
    private VocabularyQuestionService vocabularyQuestionService;

    @GetMapping
    public ResponseEntity<List<VocabularyQuestion>> getAllVocabularyQuestions() {
        List<VocabularyQuestion> vocabularyQuestions = vocabularyQuestionService.getAllVocabularyQuestions();
        return new ResponseEntity<>(vocabularyQuestions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocabularyQuestion> getVocabularyQuestionById(@PathVariable Integer id) {
        VocabularyQuestion vocabularyQuestion = vocabularyQuestionService.getVocabularyQuestionById(id);
        if (vocabularyQuestion != null) {
            return new ResponseEntity<>(vocabularyQuestion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createVocabularyQuestion(@RequestBody VocabularyQuestionDto vocabularyQuestionDto) {
        VocabularyQuestion createdVocabularyQuestion = vocabularyQuestionService.createVocabularyQuestion(vocabularyQuestionDto);
        if (createdVocabularyQuestion != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm câu hỏi từ vựng theo chủ đề thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateVocabularyQuestion(@PathVariable Integer id, @RequestBody VocabularyQuestionDto vocabularyQuestionDto) {
        VocabularyQuestion updatedVocabularyQuestion = vocabularyQuestionService.updateVocabularyQuestion(id, vocabularyQuestionDto);
        if (updatedVocabularyQuestion != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật câu hỏi từ vựng theo chủ đề thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteVocabularyQuestion(@PathVariable Integer id) {
        vocabularyQuestionService.deleteVocabularyQuestion(id);
        return ResponseEntity.ok(new MessageResponse("Xóa câu hỏi từ vựng theo chủ đề thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateVocabularyQuestionStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            VocabularyQuestion vocabularyQuestion = vocabularyQuestionService.getVocabularyQuestionById(id);
            if (vocabularyQuestion != null) {
                vocabularyQuestion.setQuestionStatus(newStatus);
                vocabularyQuestionService.updateVocabularyQuestionStatus(vocabularyQuestion);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái câu hỏi từ vựng theo chủ đề thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách nội dung ngữ pháp theo topic_id
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<List<VocabularyQuestion>> getVocabularyQuestionsByTopicId(@PathVariable Integer topicId) {
        List<VocabularyQuestion> vocabularyQuestions = vocabularyQuestionService.getVocabularyQuestionsByTopicId(topicId);
        if (vocabularyQuestions != null && !vocabularyQuestions.isEmpty()) {
            return new ResponseEntity<>(vocabularyQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //  Người dùng
    @GetMapping("/by-topic/{topicId}/enable")
    public ResponseEntity<List<VocabularyQuestion>> getEnableVocabularyQuestionsByTopicId(@PathVariable Integer topicId) {
        List<VocabularyQuestion> vocabularyQuestions = vocabularyQuestionService.getVocabularyQuestionsByTopicId(topicId);

        List<VocabularyQuestion> filteredvocabularyQuestions = vocabularyQuestions.stream()
                .filter(vocabularyQuestion -> vocabularyQuestion.getQuestionStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredvocabularyQuestions.isEmpty()) {
            return new ResponseEntity<>(filteredvocabularyQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
