package com.backend.spring.service;

import com.backend.spring.entity.Lesson;
import com.backend.spring.entity.Section;
import com.backend.spring.payload.request.LessonDto;
import com.backend.spring.repository.LessonRepository;
import com.backend.spring.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson getLessonById(Integer id) {
        return lessonRepository.findById(id).orElse(null);
    }

    public Lesson createLesson(LessonDto lessonDto) {
        Optional<Section> sectionOptional = sectionRepository.findById(lessonDto.getSectionId());
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            Lesson lesson = new Lesson();

            lesson.setSection(section);
            lesson.setLessonName(lessonDto.getLessonName());
            lesson.setLessonStatus(lessonDto.getLessonStatus());
            lesson.setCreatedAt(LocalDateTime.now());
            lesson.setUpdatedAt(LocalDateTime.now());

            return lessonRepository.save(lesson);
        }
        return null;
    }

    public void deleteLesson(Integer id) {
        lessonRepository.deleteById(id);
    }

    public Lesson updateLesson(Integer id, LessonDto lessonDto) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(id);
        Optional<Section> sectionOptional = sectionRepository.findById(lessonDto.getSectionId());
        if (lessonOptional.isPresent() && sectionOptional.isPresent()) {

            Lesson lesson = lessonOptional.get();
            Section section = sectionOptional.get();

            lesson.setSection(section);
            lesson.setLessonName(lessonDto.getLessonName());
            lesson.setLessonStatus(lessonDto.getLessonStatus());
            lesson.setUpdatedAt(LocalDateTime.now());

            return lessonRepository.save(lesson);
        }
        return null;
    }

    @Transactional
    public void updateLessonStatus(Lesson lesson) {
        lesson.setUpdatedAt(LocalDateTime.now());
        lessonRepository.save(lesson);
    }

    public List<Lesson> getLessonsBySectionId(Integer sectionId) {
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            return lessonRepository.findBySection(section);
        }
        return null;
    }

    public String getLessonNameById(Integer id) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(id);
        return lessonOptional.map(Lesson::getLessonName).orElse(null);
    }

}
