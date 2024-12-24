package com.backend.spring.service;

import com.backend.spring.entity.Grammar;
import com.backend.spring.entity.Lesson;
import com.backend.spring.entity.Topic;
import com.backend.spring.payload.request.GrammarDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.repository.GrammarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GrammarService {

    @Autowired
    private GrammarRepository grammarRepository;

    public List<Grammar> getAllGrammar() {
        return grammarRepository.findAll();
    }

    public Grammar getGrammarById(Integer id) {
        return grammarRepository.findById(id).orElse(null);
    }

    @Transactional
    public Grammar createGrammar(GrammarDto grammarDto) {
        Grammar grammar = new Grammar(grammarDto.getGrammarName(), grammarDto.getGrammarStatus());
        return grammarRepository.save(grammar);
    }

    @Transactional
    public Grammar updateGrammar(Integer id, GrammarDto grammarDto) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(id);
        if (grammarOptional.isPresent()) {
            Grammar existingGrammar = grammarOptional.get();
            existingGrammar.setGrammarName(grammarDto.getGrammarName());
            existingGrammar.setGrammarStatus(grammarDto.getGrammarStatus());
            return grammarRepository.save(existingGrammar);
        }
        return null;
    }


    @Transactional
    public void updateGrammarStatus(Grammar grammar) {
        grammar.setUpdatedAt(LocalDateTime.now());
        grammarRepository.save(grammar);
    }

    @Transactional
    public void deleteGrammar(Integer id) {
        grammarRepository.deleteById(id);
    }

    public String getGrammarNameById(Integer id) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(id);
        return grammarOptional.map(Grammar::getGrammarName).orElse(null);
    }

    public boolean isGrammarNameExists(String grammarName) {
        return grammarRepository.existsByGrammarName(grammarName);
    }

    public boolean isGrammarNameExists(String grammarName, Integer id) {
        return grammarRepository.existsByGrammarNameAndGrammarIdNot(grammarName, id);
    }
}
