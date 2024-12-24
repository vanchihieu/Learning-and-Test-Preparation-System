package com.backend.spring.controller;

import com.backend.spring.entity.Topic;
import com.backend.spring.payload.request.TopicDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.TopicService;
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
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

//  Admin
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topicList = topicService.getAllTopics();
        return new ResponseEntity<>(topicList, HttpStatus.OK);
    }

//  Người dùng
    @GetMapping("/enable")
    public ResponseEntity<List<Topic>> getAllEnableTopics() {
        List<Topic> topicList = topicService.getAllTopics();

        // Lọc danh sách chỉ giữ lại các Topic có topicStatus là 1
        List<Topic> filteredTopics = topicList.stream()
                .filter(topic -> topic.getTopicStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredTopics.isEmpty()) {
            return new ResponseEntity<>(filteredTopics, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Integer id) {
        Topic topic = topicService.getTopicById(id);
        if (topic != null) {
            return new ResponseEntity<>(topic, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createTopic(@ModelAttribute TopicDto topicDto) {
        try {
            // Kiểm tra xem tên topic đã tồn tại chưa
            if (topicService.isTopicNameExists(topicDto.getTopicName())) {
                return new ResponseEntity<>(new MessageResponse("Tên chủ đề đã tồn tại"), HttpStatus.BAD_REQUEST);
            }

            Topic createdTopic = topicService.createTopic(topicDto);
            return ResponseEntity.ok(new MessageResponse("Thêm chủ đề thành công"));
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateTopic(@PathVariable Integer id, @ModelAttribute TopicDto topicDto) {
        try {
            // Kiểm tra trùng lặp tên topic (nếu tên đã thay đổi)
            if (topicService.isTopicNameExists(topicDto.getTopicName(), id)) {
                return new ResponseEntity<>(new MessageResponse("Tên chủ đề đã tồn tại"), HttpStatus.BAD_REQUEST);
            }
            Topic updatedTopic = topicService.updateTopic(id, topicDto);
            if (updatedTopic != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật chủ đê thành công"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTopic(@PathVariable Integer id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok(new MessageResponse("Xóa chủ đề thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateTopicStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Topic topic = topicService.getTopicById(id);
            if (topic != null) {
                topic.setTopicStatus(newStatus);
                topicService.updateTopicStatus(topic);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái chủ đề thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadTemplate() {
        // Tên file mẫu
        String filename = "topic_template.xlsx";

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

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> uploadTopicsFromExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn file để upload."), HttpStatus.BAD_REQUEST);
        }

        try {
            topicService.uploadTopicFromExcel(file);
            return ResponseEntity.ok(new MessageResponse("Upload thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Upload thất bại: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
