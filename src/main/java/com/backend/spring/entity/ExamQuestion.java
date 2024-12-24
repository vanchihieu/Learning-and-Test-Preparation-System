package com.backend.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_question_id")
    private Integer examQuestionId;

    @Column(name = "question_content", columnDefinition = "TEXT")
    private String questionContent;

    @Column(name = "option_a", columnDefinition = "TEXT")
    private String optionA;

    @Column(name = "option_b", columnDefinition = "TEXT")
    private String optionB;

    @Column(name = "option_c", columnDefinition = "TEXT")
    private String optionC;

    @Column(name = "option_d", columnDefinition = "TEXT")
    private String optionD;

    @Column(name = "correct_option", columnDefinition = "TEXT")
    private String correctOption;

    @Column(name = "question_type", columnDefinition = "TEXT")
    private String questionType;

    @Column(name = "question_image", columnDefinition = "VARCHAR(255)")
    private String questionImage;

    @Column(name = "question_script", nullable = false, columnDefinition = "TEXT")
    private String questionScript;

    @Column(name = "question_audio", columnDefinition = "VARCHAR(255)")
    private String questionAudio;

    @Column(name = "question_explanation", nullable = false,  columnDefinition = "TEXT")
    private String questionExplanation;

    @Column(name = "question_status", nullable = false)
    private Integer questionStatus = 1;

    @Column(name = "question_passage", nullable = false, columnDefinition = "TEXT")
    private String questionPassage;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "question_part", nullable = false)
    private Integer questionPart;
}
