package com.backend.spring.controller;

import com.backend.spring.entity.ScoreTable;
import com.backend.spring.payload.request.ScoreTableDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.ScoreTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/score-table")
public class ScoreTableController {

    @Autowired
    private ScoreTableService scoreTableService;

    @GetMapping
    public ResponseEntity<List<ScoreTable>> getAllScores() {
        List<ScoreTable> scoreList = scoreTableService.getAllScores();
        return new ResponseEntity<>(scoreList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScoreTable> getScoreById(@PathVariable Integer id) {
        ScoreTable scoreTable = scoreTableService.getScoreById(id);
        if (scoreTable != null) {
            return new ResponseEntity<>(scoreTable, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createScore(@RequestBody ScoreTableDto scoreTableDto) {
        ScoreTable createdScore = scoreTableService.createScore(scoreTableDto);
        return ResponseEntity.ok(new MessageResponse("Thêm điểm thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateScore(@PathVariable Integer id, @RequestBody ScoreTableDto scoreTableDto) {
        ScoreTable updatedScore = scoreTableService.updateScore(id, scoreTableDto);
        if (updatedScore != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật điểm thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteScore(@PathVariable Integer id) {
        scoreTableService.deleteScore(id);
        return ResponseEntity.ok(new MessageResponse("Xóa điểm thành công!"));
    }

    @GetMapping("/listening-scores")
    public ResponseEntity<List<ScoreTable>> getListeningScores() {
        List<ScoreTable> listeningScores = scoreTableService.getListeningScores();
        return new ResponseEntity<>(listeningScores, HttpStatus.OK);
    }

    @GetMapping("/reading-scores")
    public ResponseEntity<List<ScoreTable>> getReadingScores() {
        List<ScoreTable> readingScores = scoreTableService.getReadingScores();
        return new ResponseEntity<>(readingScores, HttpStatus.OK);
    }

}
