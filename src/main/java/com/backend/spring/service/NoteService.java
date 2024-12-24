package com.backend.spring.service;

import com.backend.spring.entity.User;
import com.backend.spring.entity.Note;
import com.backend.spring.payload.request.NoteDto;
import com.backend.spring.repository.NoteRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Note createNote(NoteDto noteDto) {
        Optional<User> userOptional = userRepository.findById(noteDto.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Note note = new Note();
            note.setUser(user);
            note.setTitle(noteDto.getTitle());
            note.setContent(noteDto.getContent());
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            return noteRepository.save(note);
        }
        return null;
    }

    @Transactional
    public Note updateNote(Long id, NoteDto noteDto) {
        Optional<Note> noteOptional = noteRepository.findById(Math.toIntExact(id));
        if (noteOptional.isPresent()) {
            Note existingNote = noteOptional.get();
            existingNote.setTitle(noteDto.getTitle());
            existingNote.setContent(noteDto.getContent());
            existingNote.setUpdatedAt(LocalDateTime.now());
            return noteRepository.save(existingNote);
        }
        return null;
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(Math.toIntExact(id)).orElse(null);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Transactional
    public List<Note> getAllNotesByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return noteRepository.findByUser(user);
        }

        return null;
    }
}
