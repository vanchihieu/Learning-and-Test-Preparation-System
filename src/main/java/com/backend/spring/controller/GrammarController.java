package com.backend.spring.controller;

import com.backend.spring.entity.Grammar;
import com.backend.spring.payload.request.GrammarDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.GrammarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/grammar")
public class GrammarController {

    @Autowired
    private GrammarService grammarService;

//  Admin
    @GetMapping
    public ResponseEntity<List<Grammar>> getAllGrammar() {
        List<Grammar> grammarList = grammarService.getAllGrammar();
        return new ResponseEntity<>(grammarList, HttpStatus.OK);
    }

//  Người dùng
    @GetMapping("/enable")
    public ResponseEntity<List<Grammar>> getAllEnableGrammar() {
        List<Grammar> grammarList = grammarService.getAllGrammar();

        // Lọc danh sách chỉ giữ lại các Grammar có grammarStatus là 1
        List<Grammar> filteredGrammarList = grammarList.stream()
                .filter(grammar -> grammar.getGrammarStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredGrammarList.isEmpty()) {
            return new ResponseEntity<>(filteredGrammarList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grammar> getGrammarById(@PathVariable Integer id) {
        Grammar grammar = grammarService.getGrammarById(id);
        if (grammar != null) {
            return new ResponseEntity<>(grammar, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getGrammarNameById(@PathVariable Integer id) {
        String grammarName = grammarService.getGrammarNameById(id);
        if (grammarName != null) {
            return new ResponseEntity<>(grammarName, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createGrammar(@RequestBody GrammarDto grammarDto) {
        System.out.println(grammarDto);

        // Kiểm tra xem tên grammar đã tồn tại chưa
        if (grammarService.isGrammarNameExists(grammarDto.getGrammarName())) {
            return new ResponseEntity<>(new MessageResponse("Tên ngữ pháp đã tồn tại"), HttpStatus.BAD_REQUEST);
        }

        Grammar createdGrammar = grammarService.createGrammar(grammarDto);
        return ResponseEntity.ok(new MessageResponse("Thêm ngữ pháp thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateGrammar(@PathVariable Integer id, @RequestBody GrammarDto grammarDto) {
        // Kiểm tra trùng lặp tên grammar (nếu tên đã thay đổi)
        if (grammarService.isGrammarNameExists(grammarDto.getGrammarName(), id)) {
            return new ResponseEntity<>(new MessageResponse("Tên ngữ pháp tồn tại"), HttpStatus.BAD_REQUEST);
        }

        Grammar updatedGrammar = grammarService.updateGrammar(id, grammarDto);
        if (updatedGrammar != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật ngữ pháp thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGrammar(@PathVariable Integer id) {
        grammarService.deleteGrammar(id);
        return ResponseEntity.ok(new MessageResponse("Xóa ngữ pháp thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateGrammarStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Grammar grammar = grammarService.getGrammarById(id);
            if (grammar != null) {
                grammar.setGrammarStatus(newStatus);
                grammarService.updateGrammarStatus(grammar);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái ngữ pháp thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
