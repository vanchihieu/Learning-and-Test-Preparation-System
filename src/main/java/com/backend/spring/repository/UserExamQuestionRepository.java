package com.backend.spring.repository;

import com.backend.spring.entity.UserExam;
import com.backend.spring.entity.UserExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserExamQuestionRepository extends JpaRepository<UserExamQuestion, Integer> {

    List<UserExamQuestion> findByUserExam(UserExam userExam);

    List<UserExamQuestion> findByUserExamIn(List<UserExam> userExams);
}

