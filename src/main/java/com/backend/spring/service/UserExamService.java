package com.backend.spring.service;

import com.backend.spring.entity.*;
import com.backend.spring.payload.request.UserExamDto;
import com.backend.spring.repository.ExamRepository;
import com.backend.spring.repository.UserExamRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserExamService {

    @Autowired
    private UserExamRepository userExamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    public List<UserExam> getAllUserExams() {
        return userExamRepository.findAll();
    }
    public UserExam getUserExamById(Integer id) {
        return userExamRepository.findById(id).orElse(null);
    }

    @Transactional
    public UserExam createUserExam(UserExamDto userExamDto) {
        Optional<User> userOptional = userRepository.findById(userExamDto.getUserId());
        Optional<Exam> examOptional = examRepository.findById(Math.toIntExact(userExamDto.getExamId()));

        if (userOptional.isPresent() && examOptional.isPresent()) {
            User user = userOptional.get();
            Exam exam = examOptional.get();

            UserExam userExam = new UserExam();
            userExam.setUser(user);
            userExam.setExam(exam);
            userExam.setCompletionTime(userExamDto.getCompletionTime());
            userExam.setNumListeningCorrectAnswers(userExamDto.getNumListeningCorrectAnswers());
            userExam.setListeningScore(userExamDto.getListeningScore());
            userExam.setNumReadingCorrectAnswers(userExamDto.getNumReadingCorrectAnswers());
            userExam.setReadingScore(userExamDto.getReadingScore());
            userExam.setTotalScore(userExamDto.getTotalScore());

            userExam.setNumCorrectAnswers(userExamDto.getNumCorrectAnswers());
            userExam.setNumWrongAnswers(userExamDto.getNumWrongAnswers());
            userExam.setNumSkippedQuestions(userExamDto.getNumSkippedQuestions());
            userExam.setGoalScore(userExamDto.getGoalScore());
            userExam.setCreatedAt(LocalDateTime.now());

            return userExamRepository.save(userExam);
        }
        return null;
    }
    public List<UserExam> getUserExamsByExamIdAndUserId(Integer examId, Long userId) {
        Optional<Exam> examOptional = examRepository.findById(examId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (examOptional.isPresent() && userOptional.isPresent()) {
            Exam exam = examOptional.get();
            User user = userOptional.get();
            return userExamRepository.findByExamAndUser(exam,user);
        }
        return null;
    }
    public boolean hasUserExamsWithExamId(Integer examId, Long userId) {
        Optional<Exam> examOptional = examRepository.findById(examId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (examOptional.isPresent() && userOptional.isPresent()) {
            Exam exam = examOptional.get();
            User user = userOptional.get();

            List<UserExam> userExams = userExamRepository.findByExamAndUser(exam, user);
            return !userExams.isEmpty(); // Trả về true
        }
        return false; // Trả về false
    }


    public Duration getTotalCompletionTimeByUserId(Long userId) {
        List<UserExam> userExams = userExamRepository.findByUserId(userId);
        long totalSeconds = userExams.stream()
                .mapToLong(UserExam::getCompletionTime)
                .sum();

        return Duration.ofSeconds(totalSeconds);
    }

    public Map<String, Integer>getTotalExamCountsByExamNameAndType() {
        List<UserExam> userExams = userExamRepository.findAll();
        // Lọc chỉ lấy những bài thi có examType là 1(FULL TEST)
        List<UserExam> filteredExams = userExams.stream()
                .filter(userExam -> userExam.getExam().getExamType() == 1)
                .collect(Collectors.toList());
        Map<String, Integer> totalExamCountsByExamName = new HashMap<>();
        for (UserExam userExam : filteredExams) {
            String examName = userExam.getExam().getExamName();
            totalExamCountsByExamName.put(examName, totalExamCountsByExamName.getOrDefault(examName, 0) + 1);
        }

        // Sắp xếp theo chữ cái
        return new TreeMap<>(totalExamCountsByExamName);
    }

    public Map<String, Integer> getDailyExamCounts() {
        List<UserExam> userExams = userExamRepository.findAll();

        Map<String, Integer> dailyExamCounts = new HashMap<>();

        for (UserExam userExam : userExams) {
            // Lấy ngày của createdAt
            LocalDate examDate = userExam.getCreatedAt().toLocalDate();

            // Chuyển định dạng ngày thành chuỗi
            String formattedDate = examDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            dailyExamCounts.put(formattedDate, dailyExamCounts.getOrDefault(formattedDate, 0) + 1);
        }
        // Sắp xếp theo chữ cái ngày
        return new TreeMap<>(dailyExamCounts);
    }

    public List<UserExam> getUserExamsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Lấy danh sách bài thi của người dùng và lọc chỉ lấy những bài thi có examType là 1 (FULL TEST)
            return userExamRepository.findByUser(user)
                    .stream()
                    .filter(userExam -> userExam.getExam().getExamType() == 1)
                    .collect(Collectors.toList());
        }
        return null;
    }

}

