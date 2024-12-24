package com.backend.spring.controller;

import com.backend.spring.entity.GrammarContent;
import com.backend.spring.payload.request.GrammarContentDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.GrammarContentService;
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

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/grammar-content")
public class GrammarContentController {

    @Autowired
    private GrammarContentService grammarContentService;

    @GetMapping
    public ResponseEntity<List<GrammarContent>> getAllGrammarContents() {
        List<GrammarContent> grammarContents = grammarContentService.getAllGrammarContents();
        return new ResponseEntity<>(grammarContents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrammarContent> getGrammarContentById(@PathVariable Integer id) {
        GrammarContent grammarContent = grammarContentService.getGrammarContentById(id);
        if (grammarContent != null) {
            return new ResponseEntity<>(grammarContent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createGrammarContent(@RequestBody GrammarContentDto grammarContentDto) {
        GrammarContent createdGrammarContent = grammarContentService.createGrammarContent(grammarContentDto);
        if (createdGrammarContent != null) {
            return ResponseEntity.ok(new MessageResponse("Thêm nội dung ngữ pháp thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateGrammarContent(@PathVariable Integer id, @RequestBody GrammarContentDto grammarContentDto) {
        GrammarContent updatedGrammarContent = grammarContentService.updateGrammarContent(id, grammarContentDto);
        if (updatedGrammarContent != null) {
            return ResponseEntity.ok(new MessageResponse("Cập nhật nội dung ngữ pháp thành công!"));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGrammarContent(@PathVariable Integer id) {
        grammarContentService.deleteGrammarContent(id);
        return ResponseEntity.ok(new MessageResponse("Xóa nội dung ngữ pháp thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateGrammarContentStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            GrammarContent grammarContent = grammarContentService.getGrammarContentById(id);
            if (grammarContent != null) {
                grammarContent.setGrammarContentStatus(newStatus);
                grammarContentService.updateGrammarContentStatus(grammarContent);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái nội dung ngữ pháp thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() {
        // Tên file mẫu
        String filename = "grammar_content_template.xlsx";

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadGrammarContentFromExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vui lòng chọn file để upload."), HttpStatus.BAD_REQUEST);
        }
        try {
            grammarContentService.uploadGrammarContentFromExcel(file);
            return ResponseEntity.ok(new MessageResponse("Upload thành công!"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Upload thất bại: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách nội dung ngữ pháp theo grammar_id
    @GetMapping("/by-grammar/{grammarId}")
    public ResponseEntity<List<GrammarContent>> getGrammarContentsByGrammarId(@PathVariable Integer grammarId) {
        List<GrammarContent> grammarContents = grammarContentService.getGrammarContentsByGrammarId(grammarId);
        if (grammarContents != null && !grammarContents.isEmpty()) {
            return new ResponseEntity<>(grammarContents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//  Người dùng
    @GetMapping("/by-grammar/{grammarId}/enable")
    public ResponseEntity<List<GrammarContent>> getEnableGrammarContentsByGrammarId(@PathVariable Integer grammarId) {
        List<GrammarContent> grammarContents = grammarContentService.getGrammarContentsByGrammarId(grammarId);

        // Lọc danh sách chỉ giữ lại các GrammarContent có grammarContentStatus là 1
        List<GrammarContent> filteredGrammarContents = grammarContents.stream()
                .filter(grammarContent -> grammarContent.getGrammarContentStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredGrammarContents.isEmpty()) {
            return new ResponseEntity<>(filteredGrammarContents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
