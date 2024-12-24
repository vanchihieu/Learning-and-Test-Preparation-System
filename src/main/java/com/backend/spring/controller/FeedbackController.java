package com.backend.spring.controller;

import com.backend.spring.entity.Feedback;
import com.backend.spring.payload.request.FeedbackDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return new ResponseEntity<>(feedbackList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Integer id) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        if (feedback != null) {
            return new ResponseEntity<>(feedback, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createFeedback(@RequestBody FeedbackDto feedbackDto) {
        Feedback createdFeedback = feedbackService.createFeedback(feedbackDto);
        return ResponseEntity.ok(new MessageResponse("Thêm đánh giá thành công!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateFeedback(@PathVariable Integer id, @RequestBody FeedbackDto feedbackDto) {
        Feedback updatedFeedback = feedbackService.updateFeedback(id, feedbackDto);
        if (updatedFeedback != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật đánh giá thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFeedback(@PathVariable Integer id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(new MessageResponse("Xóa đánh giá thành công!"));
    }

    @GetMapping("/total")
    public ResponseEntity<Long> countTotalFeedback() {
        long totalFeedback = feedbackService.countTotalFeedback();
        return new ResponseEntity<>(totalFeedback, HttpStatus.OK);
    }

    @GetMapping("/rating-percentages")
    public ResponseEntity<Map<Integer, Double>> getFeedbackPercentagesByRating() {
        try {
            Map<Integer, Double> feedbackPercentagesByRating = feedbackService.calculatePercentageFeedbackByRating();
            return ResponseEntity.ok(feedbackPercentagesByRating);
        } catch (Exception e) {
            Map<Integer, Double> response = new HashMap<>();
            response.put(-1, 0.0); // Đặt giá trị âm để biểu thị lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/five-star")
    public ResponseEntity<List<Feedback>> getFiveStarFeedbacks() {
        List<Feedback> fiveStarFeedbacks = feedbackService.getFiveStarFeedbacks();
        return new ResponseEntity<>(fiveStarFeedbacks, HttpStatus.OK);
    }
}
