package com.backend.spring.controller;

import com.backend.spring.entity.Exam;
import com.backend.spring.payload.request.ExamDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        List<Exam> examList = examService.getAllExams();
        return new ResponseEntity<>(examList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Integer id) {
        Exam exam = examService.getExamById(id);
        if (exam != null) {
            return new ResponseEntity<>(exam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createExam(@RequestBody ExamDto examDto) {
        // Kiểm tra xem tên topic đã tồn tại chưa
        if (examService.isExamNameExists(examDto.getExamName())) {
            return new ResponseEntity<>(new MessageResponse("Tên Exam đã tồn tại"), HttpStatus.BAD_REQUEST);
        }
        Exam createdExam = examService.createExam(examDto);
        return ResponseEntity.ok(new MessageResponse("Thêm bài thi thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateExam(@PathVariable Integer id, @RequestBody ExamDto examDto) {
        // Kiểm tra trùng lặp tên topic (nếu tên đã thay đổi)
        if (examService.isExamNameExists(examDto.getExamName(), id)) {
            return new ResponseEntity<>(new MessageResponse("Tên bài thi đã tồn tại"), HttpStatus.BAD_REQUEST);
        }
        Exam updatedExam = examService.updateExam(id, examDto);
        if (updatedExam != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật bài thi thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteExam(@PathVariable Integer id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(new MessageResponse("Xóa bài thi thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateExamStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            Exam exam = examService.getExamById(id);
            if (exam != null) {
                exam.setExamStatus(newStatus);
                examService.updateExamStatus(exam);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái bài thi thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/mini-tests")
    public ResponseEntity<List<Exam>> getMiniTests() {
        List<Exam> miniTests = examService.getMiniTests();
        return new ResponseEntity<>(miniTests, HttpStatus.OK);
    }

//  Admin
    @GetMapping("/full-tests")
    public ResponseEntity<List<Exam>> getFullTests() {
        List<Exam> fullTests = examService.getFullTests();
        return new ResponseEntity<>(fullTests, HttpStatus.OK);
    }

    @GetMapping("/full-tests/enable")
    public ResponseEntity<List<Exam>> getEnableFullTests() {
        List<Exam> fullTests = examService.getFullTests();

        // Lọc danh sách chỉ giữ lại các Exam có examStatus là 1
        List<Exam> filteredFullTests = fullTests.stream()
                .filter(exam -> exam.getExamStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredFullTests.isEmpty()) {
            return new ResponseEntity<>(filteredFullTests, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/total")
    public ResponseEntity<Long> countTotalExams() {
        long totalExams = examService.countTotalExams();
        return new ResponseEntity<>(totalExams, HttpStatus.OK);
    }

}
