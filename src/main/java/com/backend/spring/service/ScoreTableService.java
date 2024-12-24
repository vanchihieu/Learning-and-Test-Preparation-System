package com.backend.spring.service;

import com.backend.spring.entity.ScoreTable;
import com.backend.spring.payload.request.ScoreTableDto;
import com.backend.spring.repository.ScoreTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScoreTableService {

    @Autowired
    private ScoreTableRepository scoreTableRepository;

    public List<ScoreTable> getAllScores() {
        return scoreTableRepository.findAll();
    }

    public ScoreTable getScoreById(Integer id) {
        return scoreTableRepository.findById(id).orElse(null);
    }

    @Transactional
    public ScoreTable createScore(ScoreTableDto scoreTableDto) {
        ScoreTable scoreTable = new ScoreTable();
        scoreTable.setScore(scoreTableDto.getScore());
        return scoreTableRepository.save(scoreTable);
    }

    @Transactional
    public ScoreTable updateScore(Integer id, ScoreTableDto scoreTableDto) {
        Optional<ScoreTable> scoreTableOptional = scoreTableRepository.findById(id);
        if (scoreTableOptional.isPresent()) {
            ScoreTable existingScoreTable = scoreTableOptional.get();
            existingScoreTable.setScore(scoreTableDto.getScore());
            return scoreTableRepository.save(existingScoreTable);
        }
        return null;
    }

    @Transactional
    public void deleteScore(Integer id) {
        scoreTableRepository.deleteById(id);
    }

    @Transactional
    public List<ScoreTable> getListeningScores() {
        return scoreTableRepository.findByType(0);
    }

    @Transactional
    public List<ScoreTable> getReadingScores() {
        return scoreTableRepository.findByType(1);
    }

}
