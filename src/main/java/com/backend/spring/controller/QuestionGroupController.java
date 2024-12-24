package com.backend.spring.controller;

import com.backend.spring.entity.QuestionGroup;
import com.backend.spring.payload.request.QuestionGroupDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.QuestionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/question-group")
public class QuestionGroupController {

    @Autowired
    private QuestionGroupService questionGroupService;

    @GetMapping
    public ResponseEntity<List<QuestionGroup>> getAllQuestionGroups() {
        List<QuestionGroup> questionGroupList = questionGroupService.getAllQuestionGroups();
        return new ResponseEntity<>(questionGroupList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionGroup> getQuestionGroupById(@PathVariable Integer id) {
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupById(id);
        if (questionGroup != null) {
            return new ResponseEntity<>(questionGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createQuestionGroup(@ModelAttribute QuestionGroupDto questionGroupDto) {
        try {
            QuestionGroup createdGroup = questionGroupService.createQuestionGroup(questionGroupDto);
            Integer createdGroupId = createdGroup.getGroupId(); // Lấy ID của đối tượng vừa tạo
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("groupId", createdGroupId);
            response.put("message", "Thêm nhóm câu hỏi thành công!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateQuestionGroup(@PathVariable Integer id, @ModelAttribute QuestionGroupDto questionGroupDto) {
        try {
            QuestionGroup updatedGroup = questionGroupService.updateQuestionGroup(id, questionGroupDto);
            if (updatedGroup != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật nhóm câu hỏi!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật nhóm câu hỏi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteQuestionGroup(@PathVariable Integer id) {
        questionGroupService.deleteQuestionGroup(id);
        return ResponseEntity.ok(new MessageResponse("Xóa nhóm câu hỏi thành công!"));
    }

}
