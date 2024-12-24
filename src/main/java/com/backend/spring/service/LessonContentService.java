package com.backend.spring.service;

import com.backend.spring.entity.Lesson;
import com.backend.spring.entity.LessonContent;
import com.backend.spring.payload.request.LessonContentDto;
import com.backend.spring.repository.LessonContentRepository;
import com.backend.spring.repository.LessonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LessonContentService {

    @Autowired
    private LessonContentRepository lessonContentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Transactional
    public LessonContent createLessonContent(LessonContentDto lessonContentDto) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonContentDto.getLessonId());
        if (lessonOptional.isPresent()) {
            Lesson lesson = lessonOptional.get();
            LessonContent lessonContent = new LessonContent();

            lessonContent.setLesson(lesson);
            lessonContent.setTitle(lessonContentDto.getTitle());
            lessonContent.setContent(lessonContentDto.getContent());
            lessonContent.setLessonContentStatus(lessonContentDto.getLessonContentStatus());

            lessonContent.setCreatedAt(LocalDateTime.now());
            lessonContent.setUpdatedAt(LocalDateTime.now());

            return lessonContentRepository.save(lessonContent);
        }
        return null;
    }

    public LessonContent updateLessonContent(Integer id, LessonContentDto lessonContentDto) {
        Optional<LessonContent> lessonContentOptional = lessonContentRepository.findById(id);
        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonContentDto.getLessonId());
        if (lessonContentOptional.isPresent() && lessonOptional.isPresent()) {
            LessonContent lessonContent = lessonContentOptional.get();
            Lesson lesson = lessonOptional.get();

            lessonContent.setLesson(lesson);
            lessonContent.setTitle(lessonContentDto.getTitle());
            lessonContent.setContent(lessonContentDto.getContent());
            lessonContent.setLessonContentStatus(lessonContentDto.getLessonContentStatus());

            lessonContent.setUpdatedAt(LocalDateTime.now());

            return lessonContentRepository.save(lessonContent);
        }
        return null;
    }

    @Transactional
    public void updateLessonContentStatus(LessonContent lessonContent) {
        lessonContent.setUpdatedAt(LocalDateTime.now());
        lessonContentRepository.save(lessonContent);
    }

    public List<LessonContent> getAllLessonContents() {
        return lessonContentRepository.findAll();
    }

    public LessonContent getLessonContentById(Integer id) {
        return lessonContentRepository.findById(id).orElse(null);
    }

    public void deleteLessonContent(Integer id) {
        lessonContentRepository.deleteById(id);
    }

    public List<LessonContent> getLessonContentsByLessonId(Integer lessonId) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonId);
        if (lessonOptional.isPresent()) {
            Lesson lesson = lessonOptional.get();
            return lessonContentRepository.findByLesson(lesson);
        }
        return null;
    }
}
