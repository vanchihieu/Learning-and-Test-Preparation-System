package com.backend.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_exam")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExam implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_exam_id")
    private Integer userExamId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "completion_time")
    private Integer completionTime;

    @Column(name = "num_listening_correct_answers")
    private Integer numListeningCorrectAnswers;

    @Column(name = "listening_score")
    private Integer listeningScore;

    @Column(name = "num_reading_correct_answers")
    private Integer numReadingCorrectAnswers;

    @Column(name = "reading_score")
    private Integer readingScore;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "num_correct_answers")
    private Integer numCorrectAnswers;

    @Column(name = "num_wrong_answers")
    private Integer numWrongAnswers;

    @Column(name = "num_skipped_questions")
    private Integer numSkippedQuestions;

    @Column(name = "goal_score")
    private Integer goalScore;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}


