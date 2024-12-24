package com.backend.spring.controller;

import com.backend.spring.entity.UserExam;
import com.backend.spring.payload.request.UserExamDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.UserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user-exam")
public class UserExamController {

    @Autowired
    private UserExamService userExamService;

    @GetMapping
    public ResponseEntity<List<UserExam>> getAllUserExams() {
        List<UserExam> userExamList = userExamService.getAllUserExams();
        return new ResponseEntity<>(userExamList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserExam> getUserExamById(@PathVariable Integer id) {
        UserExam userExam = userExamService.getUserExamById(id);
        if (userExam != null) {
            return new ResponseEntity<>(userExam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUserExam(@RequestBody UserExamDto userExamDto) {
        try {
            UserExam createdUserExam = userExamService.createUserExam(userExamDto);
            Integer userExamId = createdUserExam.getUserExamId(); // Lấy ID của đối tượng vừa tạo
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userExamId", userExamId);
            response.put("message", "Thêm kết quả bài thi của người dùng thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thêm kết quả bài thi của người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user-exams/{examId}/{userId}")
    public ResponseEntity<?> getUserExamsByExamIdAndUserId(
            @PathVariable Integer examId,
            @PathVariable Long userId
    ) {
        try {
            List<UserExam> userExams = userExamService.getUserExamsByExamIdAndUserId(examId, userId);
            // Sắp xếp danh sách userExams theo createdAt (ngày tạo) giảm dần (từ mới đến cũ)
            userExams.sort(Comparator.comparing(UserExam::getCreatedAt).reversed());

            return ResponseEntity.ok(userExams);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy bài thi người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/has-user-exams/{examId}/{userId}")
    public ResponseEntity<?> hasUserExamsWithExamId(
            @PathVariable Integer examId,
            @PathVariable Long userId
    ) {
        try {
            boolean hasUserExams = userExamService.hasUserExamsWithExamId(examId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasUserExams", hasUserExams);
            response.put("message", "Người dùng đã tham gia bài thi này!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi kiểm tra bài thi của người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }






    // Thống kê bài thi có điểm cao nhất cho mỗi người dùng
    @GetMapping("/max-scores/{userId}")
    public ResponseEntity<Map<Integer, UserExam>> getAllMaxScoresByExamId(@PathVariable Long userId) {
        List<UserExam> userExams = userExamService.getUserExamsByUserId(userId); // Lấy tất cả bài thi FULLTEST
        Map<Integer, UserExam> maxScoresByExamId = new HashMap<>();
        for (UserExam userExam : userExams) {
            int examId = userExam.getExam().getExamId();
//          Bài thi hiện tại lớn hơn bài thi đã lưu => Cập nhật
            if (!maxScoresByExamId.containsKey(examId) || userExam.getTotalScore() > maxScoresByExamId.get(examId).getTotalScore()) {
                maxScoresByExamId.put(examId, userExam);
            }
        }
        return new ResponseEntity<>(maxScoresByExamId, HttpStatus.OK);
    }

    // Thống kê điểm cao nhất hằng ngày cho mỗi người dùng
    @GetMapping("/max-scores-by-date/{userId}")
    public ResponseEntity<Map<String, UserExam>> getMaxScoresByDate(@PathVariable Long userId) {
        List<UserExam> userExams = userExamService.getUserExamsByUserId(userId); // Lấy tất cả bài thi của người dùng cụ thể
        Map<String, UserExam> maxScoresByDate = new HashMap<>();
        // Duyệt qua danh sách bài thi và chọn bài thi có điểm cao nhất cho mỗi ngày
        for (UserExam userExam : userExams) {
            String createdAtDate = userExam.getCreatedAt().toLocalDate().toString();
            if (!maxScoresByDate.containsKey(createdAtDate) || userExam.getTotalScore() > maxScoresByDate.get(createdAtDate).getTotalScore()) {
                maxScoresByDate.put(createdAtDate, userExam);
            }
        }
        return new ResponseEntity<>(maxScoresByDate, HttpStatus.OK);
    }

//  Tổng thời gian luyện tập
    @GetMapping("/total-completion-time/{userId}")
    public ResponseEntity<Long> getTotalCompletionTimeByUserId(@PathVariable Long userId) {
        try {
            Duration totalCompletionTime = userExamService.getTotalCompletionTimeByUserId(userId);
            // Trả về tổng thời gian luyện tập dưới dạng số giây
            return ResponseEntity.ok(totalCompletionTime.getSeconds());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L); // Lỗi xảy ra
        }
    }
//  Thống kê tổng số lượt thi của từng bài thi (ADMIN)
    @GetMapping("/total-exam-counts")
    public ResponseEntity<Map<String, Integer>> getTotalExamCountsByExamNameAndType() {
        try {
            Map<String, Integer> totalExamCountsByExamName = userExamService.getTotalExamCountsByExamNameAndType();
            return ResponseEntity.ok(totalExamCountsByExamName);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi truy vấn tổng số bài thi cho từng bài thi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


//  Tổng số bài thi hằng ngày (ADMIN)
    @GetMapping("/daily-exam-counts")
    public ResponseEntity<Map<String, Integer>> getDailyExamCounts() {
        try {
            Map<String, Integer> dailyExamCounts = userExamService.getDailyExamCounts();
            return ResponseEntity.ok(dailyExamCounts);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi truy vấn tổng số lượt thi hằng ngày: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Tính độ dài (length) của danh sách bài thi của người dùng theo userId
    @GetMapping("/user-exams/length/{userId}")
    public ResponseEntity<Integer> getUserExamsLengthByUserId(@PathVariable Long userId) {
        try {
            List<UserExam> userExams = userExamService.getUserExamsByUserId(userId);
            if (userExams != null) {
                return ResponseEntity.ok(userExams.size());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Không tìm thấy bài thi hoặc người dùng
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Lỗi xảy ra
        }
    }

}

