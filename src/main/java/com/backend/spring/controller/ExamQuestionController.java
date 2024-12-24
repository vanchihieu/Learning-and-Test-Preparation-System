package com.backend.spring.controller;

import com.backend.spring.entity.ExamQuestion;
import com.backend.spring.payload.request.ExamQuestionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/exam-question")
public class ExamQuestionController {

    @Autowired
    private ExamQuestionService examQuestionService;

    @GetMapping
    public ResponseEntity<List<ExamQuestion>> getAllExamQuestions() {
        List<ExamQuestion> examQuestionList = examQuestionService.getAllExamQuestions();
        return new ResponseEntity<>(examQuestionList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamQuestion> getExamQuestionById(@PathVariable Integer id) {
        ExamQuestion examQuestion = examQuestionService.getExamQuestionById(id);
        if (examQuestion != null) {
            return new ResponseEntity<>(examQuestion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createExamQuestion(@ModelAttribute ExamQuestionDto examQuestionDto) {
        try {
            ExamQuestion createdExamQuestion = examQuestionService.createExamQuestion(examQuestionDto);
            return ResponseEntity.ok(new MessageResponse("Thêm câu hỏi bài thi thành công!"));
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateExamQuestion(@PathVariable Integer id, @ModelAttribute ExamQuestionDto examQuestionDto) {
        try {
            ExamQuestion updatedExamQuestion = examQuestionService.updateExamQuestion(id, examQuestionDto);
            if (updatedExamQuestion != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật bài thi thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteExamQuestion(@PathVariable Integer id) {
        examQuestionService.deleteExamQuestion(id);
        return ResponseEntity.ok(new MessageResponse("Xóa bài thi thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateExamQuestionStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            ExamQuestion examQuestion = examQuestionService.getExamQuestionById(id);
            if (examQuestion != null) {
                examQuestion.setQuestionStatus(newStatus);
                examQuestionService.updateExamQuestionStatus(examQuestion);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái bài thi thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() {
        // Tên file mẫu
        String filename = "exam_question_fulltest_template.xlsx";
        // Đọc file mẫu từ thư mục tài liệu tĩnh
        Resource resource = new ClassPathResource("/static/export-template/" + filename);

        // Cài đặt tiêu đề và loại dữ liệu trả về
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Trả về file mẫu dưới dạng tệp tin (Resource)
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/by-exam/{examId}")
    public ResponseEntity<List<ExamQuestion>> getQuestionsByExamId(@PathVariable Integer examId) {
        List<ExamQuestion> examQuestionList = examQuestionService.getExamQuestionsByExamId(examId);
        if (examQuestionList != null) {
            return new ResponseEntity<>(examQuestionList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-by-exam/{examId}")
    public ResponseEntity<MessageResponse> deleteExamQuestionsByExamId(@PathVariable Integer examId) {
        examQuestionService.deleteExamQuestionsByExamId(examId);
        return ResponseEntity.ok(new MessageResponse("Xóa câu hỏi theo exam_id thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-excel")
    public ResponseEntity<MessageResponse> uploadExamQuestionsFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("examId") Integer examId) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn file upload."), HttpStatus.BAD_REQUEST);
        }
        try {
            examQuestionService.uploadExamQuestionsFromExcel(file, examId);
            return ResponseEntity.ok(new MessageResponse("Upload thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Upload thất bại: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-image")
    public ResponseEntity<MessageResponse> uploadExamQuestionImages(
            @RequestParam("questionImage") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn ảnh để upload."), HttpStatus.BAD_REQUEST);
        }
        try {
            // Đường dẫn thư mục tĩnh cho ảnh
            String imagePath = "images/";
            Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

            if (!Files.exists(uploadImagePath)) {
                Files.createDirectories(uploadImagePath);
            }
            List<String> imageNames = new ArrayList<>();

            for (MultipartFile file : files) {
                String imageName = file.getOriginalFilename();
                Path imageFile = uploadImagePath.resolve(imageName);

                try (OutputStream osImage = Files.newOutputStream(imageFile)) {
                    osImage.write(file.getBytes());
                }
                imageNames.add(imageName);
            }

            return ResponseEntity.ok(new MessageResponse("Ảnh upload thành công: " + String.join(", ", imageNames)));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi upload ảnh: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload-audio")
    public ResponseEntity<MessageResponse> uploadExamQuestionAudios(
            @RequestParam("questionAudio") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn audio để upload"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Đường dẫn thư mục tĩnh cho âm thanh
            String audioPath = "audios/";
            Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", audioPath);

            if (!Files.exists(uploadAudioPath)) {
                Files.createDirectories(uploadAudioPath);
            }

            List<String> audioNames = new ArrayList<>();

            for (MultipartFile file : files) {
                String audioName = file.getOriginalFilename();
                Path audioFile = uploadAudioPath.resolve(audioName);

                try (OutputStream osAudio = Files.newOutputStream(audioFile)) {
                    osAudio.write(file.getBytes());
                }
                audioNames.add(audioName);
            }

            return ResponseEntity.ok(new MessageResponse("Upload audio thành công: " + String.join(", ", audioNames)));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Upload audio thất bại: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
