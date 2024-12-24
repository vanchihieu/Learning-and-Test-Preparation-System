package com.backend.spring.service;

import com.backend.spring.entity.Question;
import com.backend.spring.entity.Test;
import com.backend.spring.entity.Section;
import com.backend.spring.payload.request.TestDto;
import com.backend.spring.repository.QuestionRepository;
import com.backend.spring.repository.TestRepository;
import com.backend.spring.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public Test getTestById(Integer id) {
        return testRepository.findById(id).orElse(null);
    }

    public Test createTest(TestDto testDto) {
        Optional<Section> sectionOptional = sectionRepository.findById(testDto.getSectionId());
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            Test test = new Test();

            test.setSection(section);
            test.setTestName(testDto.getTestName());
            test.setTestParticipants(testDto.getTestParticipants());
            test.setTestStatus(testDto.getTestStatus());

            return testRepository.save(test);
        }
        return null;
    }

    public void deleteTest(Integer id) {
        testRepository.deleteById(id);
    }

    public Test updateTest(Integer id, TestDto testDto) {
        Optional<Test> testOptional = testRepository.findById(id);
        Optional<Section> sectionOptional = sectionRepository.findById(testDto.getSectionId());
        if (testOptional.isPresent() && sectionOptional.isPresent()) {
            Test test = testOptional.get();
            Section section = sectionOptional.get();

            test.setSection(section);
            test.setTestName(testDto.getTestName());
            test.setTestParticipants(testDto.getTestParticipants());
            test.setTestStatus(testDto.getTestStatus());

            return testRepository.save(test);
        }
        return null;
    }

    @Transactional
    public void updateTestStatus(Test test) {
        test.setUpdatedAt(LocalDateTime.now());
        testRepository.save(test);
    }

    @Transactional
    public void updateTestParticipants(Integer testId, Integer participants) {
        Optional<Test> testOptional = testRepository.findById(testId);
        if (testOptional.isPresent()) {
            Test test = testOptional.get();
            test.setTestParticipants(participants);
        }
    }

    public List<Test> getTestsBySectionId(Integer sectionId) {
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            return testRepository.findBySection(section);
        }
        return null;
    }

    @Transactional
    public Test updateQuestionsInTest(Integer testId, List<Integer> questionIds) {
        Optional<Test> testOptional = testRepository.findById(testId);
        if (testOptional.isPresent()) {
            Test test = testOptional.get();

            // Get new questions
            List<Question> newQuestions = questionRepository.findAllById(questionIds);
            // Clear existing questions and add new questions (Hash set)
            test.getQuestions().clear();
            test.getQuestions().addAll(newQuestions);

            return testRepository.save(test);
        }
        return null;
    }

    public Set<Question> getQuestionsByTestId(Integer testId) {
        Optional<Test> testOptional = testRepository.findById(testId);
        if (testOptional.isPresent()) {
            Test test = testOptional.get();
            Set<Question> questions = test.getQuestions();

            // Sắp xếp câu hỏi theo trường "questionId"
            questions = questions.stream()
                    .sorted(Comparator.comparing(Question::getQuestionId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            return questions;
        }
        return Collections.emptySet();
    }

    public long countQuestionUsage(Integer questionId) {
        long count = 0;
        for (Test test : testRepository.findAll()) {
            if (test.getQuestions().stream().anyMatch(question -> question.getQuestionId().equals(questionId))) {
                count++;
            }
        }
        return count;
    }

    public String getTestNameById(Integer id) {
        Optional<Test> testOptional = testRepository.findById(id);
        return testOptional.map(Test::getTestName).orElse(null);
    }

}
