package com.backend.spring.controller;

import com.backend.spring.entity.Note;
import com.backend.spring.payload.request.NoteDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody NoteDto noteDto) {
        try {
            Note createdNote = noteService.createNote(noteDto);
            return ResponseEntity.ok(new MessageResponse("Tạo ghi chú thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi tạo ghi chú: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody NoteDto noteDto) {
        try {
            Note updatedNote = noteService.updateNote(id, noteDto);
            if (updatedNote != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật ghi chú thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật ghi chú: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        if (note != null) {
            return new ResponseEntity<>(note, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> noteList = noteService.getAllNotes();
        return new ResponseEntity<>(noteList, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getAllNotesByUserId(@PathVariable Long userId) {
        List<Note> noteList = noteService.getAllNotesByUserId(userId);
        if (noteList != null) {
            return new ResponseEntity<>(noteList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
