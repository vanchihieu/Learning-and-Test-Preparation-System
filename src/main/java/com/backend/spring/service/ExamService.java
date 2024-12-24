package com.backend.spring.service;

import com.backend.spring.entity.Exam;
import com.backend.spring.payload.request.ExamDto;
import com.backend.spring.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam getExamById(Integer id) {
        return examRepository.findById(id).orElse(null);
    }

    @Transactional
    public Exam createExam(ExamDto examDto) {
        Exam exam = new Exam(examDto.getExamName(), examDto.getExamType(), examDto.getExamStatus());
        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(Integer id, ExamDto examDto) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isPresent()) {
            Exam existingExam = examOptional.get();
            existingExam.setExamName(examDto.getExamName());
            existingExam.setExamType(examDto.getExamType());
            existingExam.setExamStatus(examDto.getExamStatus());
            // Tính toán lại examDuration dựa vào loại kỳ thi mới
            if (examDto.getExamType() == 0) { // mini exam
                existingExam.setExamDuration(3600); // 60 minutes in seconds
            } else if (examDto.getExamType() == 1) { // full exam
                existingExam.setExamDuration(7200); // 1 hour in seconds
            } else {
                throw new IllegalArgumentException("Invalid exam type.");
            }
            return examRepository.save(existingExam);
        }
        return null;
    }

    @Transactional
    public void updateExamStatus(Exam exam) {
        exam.setUpdatedAt(LocalDateTime.now());
        examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Integer id) {
        examRepository.deleteById(id);
    }

    @Transactional
    public List<Exam> getMiniTests() {
        return examRepository.findByExamType(0);
    }

    @Transactional
    public List<Exam> getFullTests() {
        return examRepository.findByExamType(1);
    }

    @Transactional(readOnly = true)
    public long countTotalExams() {
        return examRepository.count();
    }

    public boolean isExamNameExists(String examName) {
        return examRepository.existsByExamName(examName);
    }

    public boolean isExamNameExists(String examName, Integer id) {
        return examRepository.existsByExamNameAndExamIdNot(examName, id);
    }
}
