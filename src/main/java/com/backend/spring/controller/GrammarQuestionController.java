package com.backend.spring.controller;

import com.backend.spring.entity.GrammarQuestion;
import com.backend.spring.payload.request.GrammarQuestionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.GrammarQuestionService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/grammar-question")
public class GrammarQuestionController {

    @Autowired
    private GrammarQuestionService grammarQuestionService;

    @GetMapping
    public ResponseEntity<List<GrammarQuestion>> getAllGrammarQuestions() {
        List<GrammarQuestion> grammarQuestions = grammarQuestionService.getAllGrammarQuestions();
        return new ResponseEntity<>(grammarQuestions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrammarQuestion> getGrammarQuestionById(@PathVariable Integer id) {
        GrammarQuestion grammarQuestion = grammarQuestionService.getGrammarQuestionById(id);
        if (grammarQuestion != null) {
            return new ResponseEntity<>(grammarQuestion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createGrammarQuestion(@RequestBody GrammarQuestionDto grammarQuestionDto) {
        GrammarQuestion createdGrammarQuestion = grammarQuestionService.createGrammarQuestion(grammarQuestionDto);
        if (createdGrammarQuestion != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm câu hỏi ngữ pháp thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateGrammarQuestion(@PathVariable Integer id, @RequestBody GrammarQuestionDto grammarQuestionDto) {
        GrammarQuestion updatedGrammarQuestion = grammarQuestionService.updateGrammarQuestion(id, grammarQuestionDto);
        if (updatedGrammarQuestion != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật câu hỏi ngữ pháp thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGrammarQuestion(@PathVariable Integer id) {
        grammarQuestionService.deleteGrammarQuestion(id);
        return ResponseEntity.ok(new MessageResponse("Xóa câu hỏi ngữ pháp thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateGrammarQuestionStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            GrammarQuestion grammarQuestion = grammarQuestionService.getGrammarQuestionById(id);
            if (grammarQuestion != null) {
                grammarQuestion.setQuestionStatus(newStatus);
                grammarQuestionService.updateGrammarQuestionStatus(grammarQuestion);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái câu hỏi ngữ pháp thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách nội dung ngữ pháp theo grammar_id
    @GetMapping("/by-grammar/{grammarId}")
    public ResponseEntity<List<GrammarQuestion>> getGrammarQuestionsByGrammarId(@PathVariable Integer grammarId) {
        List<GrammarQuestion> grammarQuestions = grammarQuestionService.getGrammarQuestionsByGrammarId(grammarId);
        if (grammarQuestions != null && !grammarQuestions.isEmpty()) {
            return new ResponseEntity<>(grammarQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //  Người dùng
    @GetMapping("/by-grammar/{grammarId}/enable")
    public ResponseEntity<List<GrammarQuestion>> getEnableGrammarQuestionsByGrammarId(@PathVariable Integer grammarId) {
        List<GrammarQuestion> grammarQuestions = grammarQuestionService.getGrammarQuestionsByGrammarId(grammarId);

        // Lọc danh sách chỉ giữ lại các GrammarContent có grammarContentStatus là 1
        List<GrammarQuestion> filteredGrammarQuestions = grammarQuestions.stream()
                .filter(grammarQuestion -> grammarQuestion.getQuestionStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredGrammarQuestions.isEmpty()) {
            return new ResponseEntity<>(filteredGrammarQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
