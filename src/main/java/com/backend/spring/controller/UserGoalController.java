package com.backend.spring.controller;

import com.backend.spring.entity.UserGoal;
import com.backend.spring.payload.request.UserGoalDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.UserGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-goal")
public class UserGoalController {

    @Autowired
    private UserGoalService userGoalService;

    @PostMapping
    public ResponseEntity<?> createUserGoal(@RequestBody UserGoalDto userGoalDto) {
        try {
            UserGoal createdUserGoal = userGoalService.createUserGoal(userGoalDto);
            return ResponseEntity.ok(new MessageResponse("Tạo mục tiêu thành công!"));

        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm mục tiêu: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGoal> getUserGoalById(@PathVariable Long id) {
        UserGoal userGoal = userGoalService.getUserGoalById(id);
        if (userGoal != null) {
            return new ResponseEntity<>(userGoal, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserGoal>> getAllUserGoals() {
        List<UserGoal> userGoalList = userGoalService.getAllUserGoals();
        return new ResponseEntity<>(userGoalList, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<UserGoal> getUserGoalByUserId(@PathVariable Long userId) {
        UserGoal userGoal = userGoalService.getUserGoalByUserId(userId);
        if (userGoal != null) {
            return new ResponseEntity<>(userGoal, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/has-user-goal/{userId}")
    public ResponseEntity<?> hasUserExamsWithExamId(
            @PathVariable Long userId
    ) {
        try {
            boolean hasUserGoal = userGoalService.hasUserGoalWithUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasUserGoal", hasUserGoal);
            response.put("message", "Nguoi dung co tham gia");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserGoalByUserId(@PathVariable Long userId, @RequestBody UserGoalDto userGoalDto) {
        try {
            UserGoal updatedUserGoal = userGoalService.updateUserGoalByUserId(userId, userGoalDto);
            if (updatedUserGoal != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật điểm mục tiêu thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật điểm mục tiêu: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
