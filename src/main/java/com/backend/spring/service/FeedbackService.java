package com.backend.spring.service;

import com.backend.spring.entity.Feedback;
import com.backend.spring.payload.request.FeedbackDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Feedback getFeedbackById(Integer id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Transactional
    public Feedback createFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback(feedbackDto.getName(), feedbackDto.getEmail(), feedbackDto.getReview(), feedbackDto.getRating());
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback updateFeedback(Integer id, FeedbackDto feedbackDto) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isPresent()) {
            Feedback existingFeedback = feedbackOptional.get();
            existingFeedback.setName(feedbackDto.getName());
            existingFeedback.setEmail(feedbackDto.getEmail());
            existingFeedback.setReview(feedbackDto.getReview());
            existingFeedback.setRating(feedbackDto.getRating());
            return feedbackRepository.save(existingFeedback);
        }
        return null;
    }

    @Transactional
    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countTotalFeedback() {
        return feedbackRepository.count();
    }

    @Transactional(readOnly = true)
    public Map<Integer, Double> calculatePercentageFeedbackByRating() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        long totalFeedback = feedbackList.size();

        Map<Integer, Long> feedbackCountByRating = feedbackList.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));

        Map<Integer, Double> percentageFeedbackByRating = new HashMap<>();

        for (Map.Entry<Integer, Long> entry : feedbackCountByRating.entrySet()) {
            int rating = entry.getKey();
            long count = entry.getValue();

            // Tính toán phần trăm và thêm vào map
            double percentage = (count * 100.0) / totalFeedback;
            percentageFeedbackByRating.put(rating, percentage);
        }

        return percentageFeedbackByRating;
    }

    @Transactional(readOnly = true)
    public List<Feedback> getFiveStarFeedbacks() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        return feedbackList.stream()
                .filter(feedback -> feedback.getRating() == 5)
                .collect(Collectors.toList());
    }

}
