package com.backend.spring.controller;

import com.backend.spring.entity.UserVocabulary;
import com.backend.spring.payload.request.UserVocabularyDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.UserVocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user-vocabulary")
public class UserVocabularyController {

    @Autowired
    private UserVocabularyService userVocabularyService;

    @PostMapping
    public ResponseEntity<?> createUserVocabulary(@RequestBody UserVocabularyDto userVocabularyDto) {
        try {
            UserVocabulary createdUserVocabulary = userVocabularyService.createUserVocabulary(userVocabularyDto);
            return ResponseEntity.ok(new MessageResponse("Thêm từ vựng cá nhân thành công!"));

        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm từ vựng cá nhân: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-vocabularies/{userId}")
    public ResponseEntity<?> getUserVocabulariesByUserId(
            @PathVariable Long userId
    ) {
        try {
            List<UserVocabulary> userVocabularies = userVocabularyService.getUserVocabulariesByUserId(userId);
            return ResponseEntity.ok(userVocabularies);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi lấy từ vựng cá nhân của người dùng: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}/{vocabularyId}")
    public ResponseEntity<?> deleteUserVocabularyByUserIdAndVocabularyId(
            @PathVariable Long userId,
            @PathVariable Long vocabularyId
    ) {
        try {
            System.out.println(userId);
            System.out.println(vocabularyId);
            userVocabularyService.deleteUserVocabularyByVocabularyIdAndUserId(userId, vocabularyId);
            return ResponseEntity.ok(new MessageResponse("Xóa từ vựng cá nhân thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi xóa từ vựng cá nhân: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

