package com.backend.spring.controller;

import com.backend.spring.entity.Lesson;
import com.backend.spring.entity.Section;
import com.backend.spring.payload.request.LessonDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lesson")
public class LessonController {

    @Autowired
    private LessonService lessonService;

//  Admin
    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        List<Lesson> lessonList = lessonService.getAllLessons();
        return new ResponseEntity<>(lessonList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Integer id) {
        Lesson lesson = lessonService.getLessonById(id);
        if (lesson != null) {
            return new ResponseEntity<>(lesson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getLessonNameById(@PathVariable Integer id) {
        String lessonName = lessonService.getLessonNameById(id);
        if (lessonName != null) {
            return new ResponseEntity<>(lessonName, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createLesson(@RequestBody LessonDto lessonDto) {
        Lesson createdLesson = lessonService.createLesson(lessonDto);
        if (createdLesson != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm bài học thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateLesson(@PathVariable Integer id, @RequestBody LessonDto lessonDto) {
        Lesson updatedLesson = lessonService.updateLesson(id, lessonDto);
        if (updatedLesson != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật bài học thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLesson(@PathVariable Integer id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(new MessageResponse("Xóa bài học thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateLessonStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Lesson lesson = lessonService.getLessonById(id);
            if (lesson != null) {
                lesson.setLessonStatus(newStatus);
                lessonService.updateLessonStatus(lesson);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái bài học thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách bài học theo section_id (Admin)
    @GetMapping("/by-section/{sectionId}")
    public ResponseEntity<List<Lesson>> getLessonsBySectionId(@PathVariable Integer sectionId) {
        List<Lesson> lessonList = lessonService.getLessonsBySectionId(sectionId);
        if (lessonList != null && !lessonList.isEmpty()) {
            return new ResponseEntity<>(lessonList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy danh sách bài học theo section_id (Người dùng)
    @GetMapping("/by-section/{sectionId}/enable")
    public ResponseEntity<List<Lesson>> getEnableLessonsBySectionId(@PathVariable Integer sectionId) {
        List<Lesson> lessonList = lessonService.getLessonsBySectionId(sectionId);
        // Lọc danh sách chỉ giữ lại các bài học có lessonStatus là 1
        List<Lesson> filteredLessonList = lessonList.stream()
                .filter(lesson -> lesson.getLessonStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredLessonList.isEmpty()) {
            return new ResponseEntity<>(filteredLessonList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
