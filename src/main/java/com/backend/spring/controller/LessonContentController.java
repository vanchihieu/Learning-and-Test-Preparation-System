package com.backend.spring.controller;

import com.backend.spring.entity.LessonContent;
import com.backend.spring.payload.request.LessonContentDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.LessonContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lesson-content")
public class LessonContentController {

    @Autowired
    private LessonContentService lessonContentService;

    @GetMapping
    public ResponseEntity<List<LessonContent>> getAllLessonContents() {
        List<LessonContent> lessonContents = lessonContentService.getAllLessonContents();
        return new ResponseEntity<>(lessonContents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonContent> getLessonContentById(@PathVariable Integer id) {
        LessonContent lessonContent = lessonContentService.getLessonContentById(id);
        if (lessonContent != null) {
            return new ResponseEntity<>(lessonContent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createLessonContent(@RequestBody LessonContentDto lessonContentDto) {
        LessonContent createdLessonContent = lessonContentService.createLessonContent(lessonContentDto);
        if (createdLessonContent != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm nội dung bài học thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateLessonContent(@PathVariable Integer id, @RequestBody LessonContentDto lessonContentDto) {
        LessonContent updatedLessonContent = lessonContentService.updateLessonContent(id, lessonContentDto);
        if (updatedLessonContent != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật nội dung bài học thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLessonContent(@PathVariable Integer id) {
        lessonContentService.deleteLessonContent(id);
        return ResponseEntity.ok(new MessageResponse("Xóa nội dung bài học thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateLessonContentStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            LessonContent lessonContent = lessonContentService.getLessonContentById(id);
            if (lessonContent != null) {
                lessonContent.setLessonContentStatus(newStatus);
                lessonContentService.updateLessonContentStatus(lessonContent);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái nội dung bài học thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách nội dung bài học theo lesson_id
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<List<LessonContent>> getLessonContentsByLessonId(@PathVariable Integer lessonId) {
        List<LessonContent> lessonContents = lessonContentService.getLessonContentsByLessonId(lessonId);
        if (lessonContents != null && !lessonContents.isEmpty()) {
            return new ResponseEntity<>(lessonContents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-lesson/{lessonId}/enable")
    public ResponseEntity<List<LessonContent>> getEnableLessonContentsByLessonId(@PathVariable Integer lessonId) {
        List<LessonContent> lessonContents = lessonContentService.getLessonContentsByLessonId(lessonId);

        // Lọc danh sách chỉ giữ lại các LessonContent có lessonContentStatus là 1
        List<LessonContent> filteredLessonContents = lessonContents.stream()
                .filter(content -> content.getLessonContentStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredLessonContents.isEmpty()) {
            return new ResponseEntity<>(filteredLessonContents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
