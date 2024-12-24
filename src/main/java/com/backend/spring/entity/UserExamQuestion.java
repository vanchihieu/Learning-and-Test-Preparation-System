package com.backend.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "user_exam_question")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExamQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_exam_question_id")
    private Integer userExamQuestionId;

    @ManyToOne
    @JoinColumn(name = "user_exam_id", nullable = false)
    private UserExam userExam;

    @ManyToOne
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

    @Column(name = "selected_option", columnDefinition = "TEXT")
    private String selectedOption;

    @Column(name = "is_correct")
    private Integer isCorrect;

}
