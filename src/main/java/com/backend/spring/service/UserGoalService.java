package com.backend.spring.service;

import com.backend.spring.entity.Topic;
import com.backend.spring.entity.User;
import com.backend.spring.entity.UserGoal;
import com.backend.spring.entity.Vocabulary;
import com.backend.spring.payload.request.UserGoalDto;
import com.backend.spring.payload.request.VocabularyDto;
import com.backend.spring.repository.UserGoalRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserGoalService {

    @Autowired
    private UserGoalRepository userGoalRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserGoal createUserGoal(UserGoalDto userGoalDto) {
        Optional<User> userOptional = userRepository.findById(userGoalDto.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            UserGoal userGoal = new UserGoal();
            userGoal.setUser(user);
            userGoal.setGoalScore(userGoalDto.getGoalScore());
            userGoal.setCreatedAt(LocalDateTime.now());
            userGoal.setUpdatedAt(LocalDateTime.now());

            return userGoalRepository.save(userGoal);
        }
        return null;
    }

    @Transactional
    public UserGoal updateUserGoalByUserId(Long userId, UserGoalDto userGoalDto) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Optional<UserGoal> existingUserGoal = userGoalRepository.findByUser(user);
            if (existingUserGoal.isPresent()) {
                UserGoal userGoal = existingUserGoal.get();
                userGoal.setGoalScore(userGoalDto.getGoalScore());
                userGoal.setUpdatedAt(LocalDateTime.now());
                return userGoalRepository.save(userGoal);
            }
        }
        return null;
    }

    public UserGoal getUserGoalById(Long id) {
        return userGoalRepository.findById(Math.toIntExact(id)).orElse(null);
    }

    public List<UserGoal> getAllUserGoals() {
        return userGoalRepository.findAll();
    }

    public UserGoal getUserGoalByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return userGoalRepository.findByUser(user).orElse(null);
        }
        return null;
    }

    public boolean hasUserGoalWithUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<UserGoal> userGoal = userGoalRepository.findByUser(user);
            return userGoal.isPresent(); // Tìm thấy (true)
        }
        return false; //Không tìm thấy bài thi hoặc người dùng
    }


}
