package com.backend.spring.controller;

import com.backend.spring.entity.Question;
import com.backend.spring.payload.request.QuestionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questionList = questionService.getAllQuestions();
        return new ResponseEntity<>(questionList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        Question question = questionService.getQuestionById(id);
        if (question != null) {
            return new ResponseEntity<>(question, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-section/{sectionId}")
    public ResponseEntity<List<Question>> getQuestionsBySectionId(@PathVariable Integer sectionId) {
        List<Question> questionList = questionService.getQuestionsBySectionId(sectionId);
        if (questionList != null && !questionList.isEmpty()) {
            // Tính toán và chèn trường "usage" cho từng câu hỏi
            questionList.forEach(question -> question.setUsage(questionService.countQuestionUsage(question.getQuestionId())));

            return new ResponseEntity<>(questionList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-question-group/{groupId}")
    public ResponseEntity<List<Question>> getQuestionsByGroupId(@PathVariable Integer groupId) {
        List<Question> questionList = questionService.getQuestionsByGroupId(groupId);
        if (questionList != null && !questionList.isEmpty()) {
            return new ResponseEntity<>(questionList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createQuestion(@ModelAttribute QuestionDto questionDto) {
        try {
//            System.out.println(questionDto);
            Question createdQuestion = questionService.createQuestion(questionDto);
            return ResponseEntity.ok(new MessageResponse("Thêm câu hỏi thành công!"));
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateQuestion(@PathVariable Integer id, @ModelAttribute QuestionDto questionDto) {
        try {
//            System.out.println(questionDto);
            Question updatedQuestion = questionService.updateQuestion(id, questionDto);
            if (updatedQuestion != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật câu hỏi thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(new MessageResponse("Xóa câu hỏi thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateQuestionStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Question question = questionService.getQuestionById(id);
            if (question != null) {
                question.setQuestionStatus(newStatus);
                questionService.updateQuestionStatus(question);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái câu hỏi thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//  Học cải thiện
    @PostMapping("/by-section-and-type")
    public ResponseEntity<List<Question>> getQuestionsBySectionIdAndType(@RequestBody Map<String, Object> request) {
        Integer sectionId = (Integer) request.get("sectionId");
        String questionType = (String) request.get("questionType");

        // Thực hiện truy vấn dữ liệu dựa trên sectionId và questionType
        List<Question> questionList = questionService.getQuestionsBySectionIdAndType(sectionId, questionType);

        if (questionList != null && !questionList.isEmpty()) {
            return new ResponseEntity<>(questionList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}

