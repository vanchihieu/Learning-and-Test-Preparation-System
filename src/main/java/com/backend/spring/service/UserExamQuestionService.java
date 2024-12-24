package com.backend.spring.service;

import com.backend.spring.entity.*;
import com.backend.spring.payload.request.UserExamQuestionDto;
import com.backend.spring.repository.UserExamQuestionRepository;
import com.backend.spring.repository.ExamQuestionRepository;
import com.backend.spring.repository.UserExamRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserExamQuestionService {

    @Autowired
    private UserExamQuestionRepository userExamQuestionRepository;

    @Autowired
    private UserExamRepository userExamRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    public List<UserExamQuestion> getAll() {
        return userExamQuestionRepository.findAll();
    }

//  Vòng lặp thêm từng câu hỏi
    public void submitAllUserExamQuestions(List<UserExamQuestionDto> userExamQuestionDtoList) {
        for (UserExamQuestionDto userExamQuestionDto : userExamQuestionDtoList) {
            Optional<UserExam> userExamOptional = userExamRepository.findById(Math.toIntExact(userExamQuestionDto.getUserExamId()));
            Optional<ExamQuestion> examQuestionOptional = examQuestionRepository.findById(userExamQuestionDto.getExamQuestionId());

            if (userExamOptional.isPresent() && examQuestionOptional.isPresent()) {
                UserExam useExam = userExamOptional.get();
                ExamQuestion examQuestion = examQuestionOptional.get();

                // Thêm examId vào ExamResult
                UserExamQuestion userExamQuestion = new UserExamQuestion();
                userExamQuestion.setUserExam(useExam);
                userExamQuestion.setExamQuestion(examQuestion);
                userExamQuestion.setSelectedOption(userExamQuestionDto.getSelectedOption());
                userExamQuestion.setIsCorrect(userExamQuestionDto.getIsCorrect());

                userExamQuestionRepository.save(userExamQuestion);
            }
        }
    }

    public void deleteAll() {
        userExamQuestionRepository.deleteAll();
    }

    public List<UserExamQuestion> getQuestionsByUserExamId(Integer userExamId) {
        Optional<UserExam> userExamOptional = userExamRepository.findById(userExamId);
        if (userExamOptional.isPresent()) {
            UserExam userExam = userExamOptional.get();
            return userExamQuestionRepository.findByUserExam(userExam);
        }
        return null;
    }

    public List<UserExamQuestion> getUserExamQuestionsByUserId(Long userId) {
        List<UserExam> userExams = userExamRepository.findByUserId(userId);
        if (userExams != null && !userExams.isEmpty()) {
            return userExamQuestionRepository.findByUserExamIn(userExams);
        }
        return null;
    }

}
