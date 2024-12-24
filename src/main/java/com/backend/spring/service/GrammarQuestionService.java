package com.backend.spring.service;

import com.backend.spring.entity.Grammar;
import com.backend.spring.entity.GrammarQuestion;
import com.backend.spring.payload.request.GrammarQuestionDto;
import com.backend.spring.repository.GrammarQuestionRepository;
import com.backend.spring.repository.GrammarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GrammarQuestionService {

    @Autowired
    private GrammarQuestionRepository grammarQuestionRepository;

    @Autowired
    private GrammarRepository grammarRepository;

    @Transactional
    public GrammarQuestion createGrammarQuestion(GrammarQuestionDto grammarQuestionDto) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarQuestionDto.getGrammarId());
        if (grammarOptional.isPresent()) {
            Grammar grammar = grammarOptional.get();
            GrammarQuestion grammarQuestion = new GrammarQuestion();

            grammarQuestion.setGrammar(grammar);
            grammarQuestion.setQuestionContent(grammarQuestionDto.getQuestionContent());
            grammarQuestion.setOptionA(grammarQuestionDto.getOptionA());
            grammarQuestion.setOptionB(grammarQuestionDto.getOptionB());
            grammarQuestion.setOptionC(grammarQuestionDto.getOptionC());
            grammarQuestion.setOptionD(grammarQuestionDto.getOptionD());
            grammarQuestion.setCorrectOption(grammarQuestionDto.getCorrectOption());
            grammarQuestion.setQuestionExplanation(grammarQuestionDto.getQuestionExplanation());
            grammarQuestion.setQuestionStatus(grammarQuestionDto.getQuestionStatus());

            grammarQuestion.setCreatedAt(LocalDateTime.now());
            grammarQuestion.setUpdatedAt(LocalDateTime.now());

            return grammarQuestionRepository.save(grammarQuestion);
        }
        return null;
    }

    public GrammarQuestion updateGrammarQuestion(Integer id, GrammarQuestionDto grammarQuestionDto) {
        Optional<GrammarQuestion> grammarQuestionOptional = grammarQuestionRepository.findById(id);
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarQuestionDto.getGrammarId());

        if (grammarQuestionOptional.isPresent() && grammarOptional.isPresent()) {

            GrammarQuestion grammarQuestion = grammarQuestionOptional.get();
            Grammar grammar = grammarOptional.get();

            grammarQuestion.setGrammar(grammar);
            grammarQuestion.setQuestionContent(grammarQuestionDto.getQuestionContent());
            grammarQuestion.setOptionA(grammarQuestionDto.getOptionA());
            grammarQuestion.setOptionB(grammarQuestionDto.getOptionB());
            grammarQuestion.setOptionC(grammarQuestionDto.getOptionC());
            grammarQuestion.setOptionD(grammarQuestionDto.getOptionD());
            grammarQuestion.setCorrectOption(grammarQuestionDto.getCorrectOption());
            grammarQuestion.setQuestionExplanation(grammarQuestionDto.getQuestionExplanation());
            grammarQuestion.setQuestionStatus(grammarQuestionDto.getQuestionStatus());

            grammarQuestion.setUpdatedAt(LocalDateTime.now());

            return grammarQuestionRepository.save(grammarQuestion);
        }
        return null;
    }

    @Transactional
    public void updateGrammarQuestionStatus(GrammarQuestion grammarQuestion) {
        grammarQuestion.setUpdatedAt(LocalDateTime.now());
        grammarQuestionRepository.save(grammarQuestion);
    }

    public List<GrammarQuestion> getAllGrammarQuestions() {
        return grammarQuestionRepository.findAll();
    }

    public GrammarQuestion getGrammarQuestionById(Integer id) {
        return grammarQuestionRepository.findById(id).orElse(null);
    }


    public void deleteGrammarQuestion(Integer id) {
        grammarQuestionRepository.deleteById(id);
    }

    public List<GrammarQuestion> getGrammarQuestionsByGrammarId(Integer grammarId) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarId);
        if (grammarOptional.isPresent()) {
            Grammar grammar = grammarOptional.get();
            return grammarQuestionRepository.findByGrammar(grammar);
        }
        return null;
    }
}
