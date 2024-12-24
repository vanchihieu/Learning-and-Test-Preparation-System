package com.backend.spring.controller;

import com.backend.spring.entity.UserExamQuestion;
import com.backend.spring.payload.request.UserExamQuestionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.UserExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-exam-questions")
public class UserExamQuestionController {

    @Autowired
    private UserExamQuestionService userExamQuestionService;

    @DeleteMapping("/delete-all")
    public ResponseEntity<MessageResponse> deleteAll() {
        userExamQuestionService.deleteAll();
        return ResponseEntity.ok(new MessageResponse("Xóa toàn bộ câu hỏi bài thi của người dùng thành công!"));
    }
    // Lấy tất cả kết quả
    @GetMapping
    public ResponseEntity<List<UserExamQuestion>> getAll() {
        List<UserExamQuestion> results = userExamQuestionService.getAll();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // Nộp bài
    @PostMapping("/submit-all")
    public ResponseEntity<MessageResponse> submitAllUserExamQuestions(@RequestBody List<UserExamQuestionDto> userExamQuestionDtoList) {
        userExamQuestionService.submitAllUserExamQuestions(userExamQuestionDtoList);
        return ResponseEntity.ok(new MessageResponse("All exam results submitted successfully!"));
    }

    // Lấy danh sách câu hỏi từ user_exam_id
    @GetMapping("/by-user-exam/{userExamId}")
    public ResponseEntity<List<UserExamQuestion>> getQuestionsByUserExamId(@PathVariable Integer userExamId) {
        List<UserExamQuestion> userExamQuestionsList = userExamQuestionService.getQuestionsByUserExamId(userExamId);
        if (userExamQuestionsList != null && !userExamQuestionsList.isEmpty()) {
            return new ResponseEntity<>(userExamQuestionsList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-user-exam/{userExamId}/grouped")
    public ResponseEntity<Map<String, List<UserExamQuestion>>> getQuestionsByUserExamIdGroupedByType(@PathVariable Integer userExamId) {
        List<UserExamQuestion> userExamQuestionsList = userExamQuestionService.getQuestionsByUserExamId(userExamId);
        if (userExamQuestionsList != null && !userExamQuestionsList.isEmpty()) {
            // Tạo một Map để nhóm câu hỏi theo questionType
            Map<String, List<UserExamQuestion>> groupedQuestions = new HashMap<>();
            for (UserExamQuestion userExamQuestion : userExamQuestionsList) {
                String questionType = userExamQuestion.getExamQuestion().getQuestionType();
                if (!groupedQuestions.containsKey(questionType)) {
                    groupedQuestions.put(questionType, new ArrayList<>());
                }
                groupedQuestions.get(questionType).add(userExamQuestion);
            }
            // Sắp xếp Map theo questionType
            Map<String, List<UserExamQuestion>> sortedGroupedQuestions = groupedQuestions
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            return new ResponseEntity<>(sortedGroupedQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//  Thống kê độ chính xác từng phân loại câu hỏi
    @GetMapping("/accuracy-by-part/{questionPart}/user/{userId}")
    public ResponseEntity<Map<String, Double>> getAccuracyByQuestionTypeForUser(
            @PathVariable("questionPart") int questionPart,
            @PathVariable("userId") Long userId) {

        List<UserExamQuestion> userExamQuestionsList = userExamQuestionService.getUserExamQuestionsByUserId(userId);
        if (userExamQuestionsList != null && !userExamQuestionsList.isEmpty()) {
            // Lọc ra các câu hỏi thuộc questionPart và có isCorrect != null
            List<UserExamQuestion> filteredQuestions = userExamQuestionsList.stream()
                    .filter(q -> q.getExamQuestion().getQuestionPart() == questionPart && q.getIsCorrect() != null)
                    .collect(Collectors.toList());

            // Tạo một Map để lưu độ chính xác từng loại câu hỏi
            Map<String, Double> accuracyByType = new HashMap<>();
            // Tính số câu đúng và tổng số câu cho từng loại câu hỏi
            for (UserExamQuestion userExamQuestion : filteredQuestions) {
                String questionType = userExamQuestion.getExamQuestion().getQuestionType();
                accuracyByType.putIfAbsent(questionType, 0.0);
                double correctCount = accuracyByType.get(questionType);
                if (userExamQuestion.getIsCorrect() == 1) {
                    accuracyByType.put(questionType, correctCount + 1);
                }
            }
            // Tính độ chính xác cho từng loại câu hỏi
            for (Map.Entry<String, Double> entry : accuracyByType.entrySet()) {
                double correctCount = entry.getValue();
                double totalCount = filteredQuestions.stream()
                        .filter(q -> q.getExamQuestion().getQuestionType().equals(entry.getKey()))
                        .count();
                double accuracy = (totalCount != 0) ? (correctCount / totalCount) * 100 : 0;

                // Làm tròn giá trị về 2 chữ số thập phân
                accuracy = Math.round(accuracy * 100.0) / 100.0;
                accuracyByType.put(entry.getKey(), accuracy);
            }

            return new ResponseEntity<>(accuracyByType, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }























}
