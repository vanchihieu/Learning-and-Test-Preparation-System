package com.backend.spring.service;

import com.backend.spring.entity.Topic;
import com.backend.spring.entity.VocabularyQuestion;
import com.backend.spring.payload.request.VocabularyQuestionDto;
import com.backend.spring.repository.TopicRepository;
import com.backend.spring.repository.VocabularyQuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VocabularyQuestionService {

    @Autowired
    private VocabularyQuestionRepository vocabularyQuestionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Transactional
    public VocabularyQuestion createVocabularyQuestion(VocabularyQuestionDto vocabularyQuestionDto) {
        Optional<Topic> topicOptional = topicRepository.findById(vocabularyQuestionDto.getTopicId());
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            VocabularyQuestion vocabularyQuestion = new VocabularyQuestion();

            vocabularyQuestion.setTopic(topic);
            vocabularyQuestion.setQuestionContent(vocabularyQuestionDto.getQuestionContent());
            vocabularyQuestion.setOptionA(vocabularyQuestionDto.getOptionA());
            vocabularyQuestion.setOptionB(vocabularyQuestionDto.getOptionB());
            vocabularyQuestion.setOptionC(vocabularyQuestionDto.getOptionC());
            vocabularyQuestion.setOptionD(vocabularyQuestionDto.getOptionD());
            vocabularyQuestion.setCorrectOption(vocabularyQuestionDto.getCorrectOption());
            vocabularyQuestion.setQuestionExplanation(vocabularyQuestionDto.getQuestionExplanation());
            vocabularyQuestion.setQuestionStatus(vocabularyQuestionDto.getQuestionStatus());

            vocabularyQuestion.setCreatedAt(LocalDateTime.now());
            vocabularyQuestion.setUpdatedAt(LocalDateTime.now());

            return vocabularyQuestionRepository.save(vocabularyQuestion);
        }
        return null;
    }

    public VocabularyQuestion updateVocabularyQuestion(Integer id, VocabularyQuestionDto vocabularyQuestionDto) {
        Optional<VocabularyQuestion> vocabularyQuestionOptional = vocabularyQuestionRepository.findById(id);
        Optional<Topic> topicOptional = topicRepository.findById(vocabularyQuestionDto.getTopicId());

        if (vocabularyQuestionOptional.isPresent() && topicOptional.isPresent()) {

            VocabularyQuestion vocabularyQuestion = vocabularyQuestionOptional.get();
            Topic topic = topicOptional.get();

            vocabularyQuestion.setTopic(topic);
            vocabularyQuestion.setQuestionContent(vocabularyQuestionDto.getQuestionContent());
            vocabularyQuestion.setOptionA(vocabularyQuestionDto.getOptionA());
            vocabularyQuestion.setOptionB(vocabularyQuestionDto.getOptionB());
            vocabularyQuestion.setOptionC(vocabularyQuestionDto.getOptionC());
            vocabularyQuestion.setOptionD(vocabularyQuestionDto.getOptionD());
            vocabularyQuestion.setCorrectOption(vocabularyQuestionDto.getCorrectOption());
            vocabularyQuestion.setQuestionExplanation(vocabularyQuestionDto.getQuestionExplanation());
            vocabularyQuestion.setQuestionStatus(vocabularyQuestionDto.getQuestionStatus());

            vocabularyQuestion.setUpdatedAt(LocalDateTime.now());

            return vocabularyQuestionRepository.save(vocabularyQuestion);
        }
        return null;
    }

    @Transactional
    public void updateVocabularyQuestionStatus(VocabularyQuestion vocabularyQuestion) {
        vocabularyQuestion.setUpdatedAt(LocalDateTime.now());
        vocabularyQuestionRepository.save(vocabularyQuestion);
    }

    public List<VocabularyQuestion> getAllVocabularyQuestions() {
        return vocabularyQuestionRepository.findAll();
    }

    public VocabularyQuestion getVocabularyQuestionById(Integer id) {
        return vocabularyQuestionRepository.findById(id).orElse(null);
    }


    public void deleteVocabularyQuestion(Integer id) {
        vocabularyQuestionRepository.deleteById(id);
    }

    public List<VocabularyQuestion> getVocabularyQuestionsByTopicId(Integer topicId) {
        Optional<Topic> topicOptional = topicRepository.findById(topicId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            return vocabularyQuestionRepository.findByTopic(topic);
        }
        return null;
    }
}
