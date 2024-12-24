package com.backend.spring.controller;

import com.backend.spring.entity.Question;
import com.backend.spring.entity.Test;
import com.backend.spring.payload.request.TestDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        List<Test> testList = testService.getAllTests();
        return new ResponseEntity<>(testList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable Integer id) {
        Test test = testService.getTestById(id);
        if (test != null) {
            return new ResponseEntity<>(test, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createTest(@RequestBody TestDto testDto) {
        Test createdTest = testService.createTest(testDto);
        if (createdTest != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm bài kiểm tra thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateTest(@PathVariable Integer id, @RequestBody TestDto testDto) {
        Test updatedTest = testService.updateTest(id, testDto);
        if (updatedTest != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật bài kiểm tra thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTest(@PathVariable Integer id) {
        testService.deleteTest(id);
        return ResponseEntity.ok(new MessageResponse("Xóa bài kiểm tra thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateTestStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Test test = testService.getTestById(id);
            if (test != null) {
                test.setTestStatus(newStatus);
                testService.updateTestStatus(test);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái bài kiểm tra thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/participants")
    public ResponseEntity<MessageResponse> updateTestParticipants(@PathVariable Integer id, @RequestBody Integer newParticipants) {
        try {
            Test test = testService.getTestById(id);
            if (test != null) {
                test.setTestParticipants(newParticipants);
                testService.updateTestParticipants(id, newParticipants);
                return ResponseEntity.ok(new MessageResponse("Cập nhật số người tham gia kiểm tra thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật số người tham gia: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get tests by sectionId (Admin)
    @GetMapping("/by-section/{sectionId}")
    public ResponseEntity<List<Test>> getTestsBySectionId(@PathVariable Integer sectionId) {
        List<Test> testList = testService.getTestsBySectionId(sectionId);
        if (testList != null && !testList.isEmpty()) {
            return new ResponseEntity<>(testList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get tests by sectionId (Người dùng)
    @GetMapping("/by-section/{sectionId}/enable")
    public ResponseEntity<List<Test>> getEnableTestsBySectionId(@PathVariable Integer sectionId) {
        List<Test> testList = testService.getTestsBySectionId(sectionId);

        // Lọc danh sách chỉ giữ lại các Test có testStatus là 1
        List<Test> filteredTests = testList.stream()
                .filter(test -> test.getTestStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredTests.isEmpty()) {
            return new ResponseEntity<>(filteredTests, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /** Thực thể yếu Test_Question **/
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/add-questions")
    public ResponseEntity<MessageResponse> addQuestionsToTest(@PathVariable Integer id, @RequestBody List<Integer> questionIds) {
        Test updatedTest = testService.updateQuestionsInTest(id, questionIds);
        if (updatedTest != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật câu hỏi bài kiểm tra thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getTestNameById(@PathVariable Integer id) {
        String testName = testService.getTestNameById(id);
        if (testName != null) {
            return new ResponseEntity<>(testName, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{testId}/questions")
    public ResponseEntity<Set<Question>> getQuestionsByTestId(@PathVariable Integer testId) {
        Set<Question> questions = testService.getQuestionsByTestId(testId);
        if (!questions.isEmpty()) {
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/question-usage/{questionId}")
    public ResponseEntity<Long> getQuestionUsageCount(@PathVariable Integer questionId) {
        long count = testService.countQuestionUsage(questionId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


}
