package com.backend.spring.repository;

import com.backend.spring.entity.Exam;
import com.backend.spring.entity.User;
import com.backend.spring.entity.UserExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamRepository extends JpaRepository<UserExam, Integer> {
    List<UserExam> findByExamAndUser(Exam exam, User user);
    List<UserExam> findByUserId(Long userId);
    List<UserExam> findByUser(User user);

}

