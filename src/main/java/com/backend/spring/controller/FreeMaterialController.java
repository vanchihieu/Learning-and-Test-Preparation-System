package com.backend.spring.controller;

import com.backend.spring.entity.FreeMaterial;
import com.backend.spring.payload.request.FreeMaterialDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.FreeMaterialService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/free-material")
public class FreeMaterialController {

    @Autowired
    private FreeMaterialService freeMaterialService;

//  Admin   
    @GetMapping
    public ResponseEntity<List<FreeMaterial>> getAllFreeMaterials() {
        List<FreeMaterial> freeMaterialList = freeMaterialService.getAllFreeMaterials();
        return new ResponseEntity<>(freeMaterialList, HttpStatus.OK);
    }

//  Người dùng
    @GetMapping("/enable")
    public ResponseEntity<List<FreeMaterial>> getAllEnableFreeMaterials() {
        List<FreeMaterial> freeMaterialList = freeMaterialService.getAllFreeMaterials();

        // Lọc danh sách chỉ giữ lại các FreeMaterial có freeMaterialStatus là 1
        List<FreeMaterial> filteredFreeMaterials = freeMaterialList.stream()
                .filter(freeMaterial -> freeMaterial.getMaterialStatus() == 1)
                .collect(Collectors.toList());

        if (!filteredFreeMaterials.isEmpty()) {
            return new ResponseEntity<>(filteredFreeMaterials, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<FreeMaterial> getFreeMaterialById(@PathVariable Integer id) {
        FreeMaterial freeMaterial = freeMaterialService.getFreeMaterialById(id);
        if (freeMaterial != null) {
            return new ResponseEntity<>(freeMaterial, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createFreeMaterial(@ModelAttribute FreeMaterialDto freeMaterialDto) {
        try {
            FreeMaterial createdFreeMaterial = freeMaterialService.createFreeMaterial(freeMaterialDto);
            return ResponseEntity.ok(new MessageResponse("Thêm tài liệu miễn phí thành công!"));
        } catch (IOException | java.io.IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateFreeMaterial(@PathVariable Integer id, @ModelAttribute FreeMaterialDto freeMaterialDto) {
        try {
            FreeMaterial updatedFreeMaterial = freeMaterialService.updateFreeMaterial(id, freeMaterialDto);
            if (updatedFreeMaterial != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật tài liệu miễn phí thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException | java.io.IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFreeMaterial(@PathVariable Integer id) {
        freeMaterialService.deleteFreeMaterial(id);
        return ResponseEntity.ok(new MessageResponse("Xóa tài liệu miễn phí thành công!"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateFreeMaterialStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            FreeMaterial freeMaterial = freeMaterialService.getFreeMaterialById(id);
            if (freeMaterial != null) {
                freeMaterial.setMaterialStatus(newStatus);
                freeMaterialService.updateFreeMaterialStatus(freeMaterial);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái tài liệu thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Long> countTotalFreeMaterials() {
        long totalFreeMaterials = freeMaterialService.countTotalFreeMaterials();
        return new ResponseEntity<>(totalFreeMaterials, HttpStatus.OK);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFreeMaterial(@PathVariable String filename) {
        // Đường dẫn tới thư mục chứa tệp PDF
        String pdfFolderPath = "static/pdfs/";

        // Tạo một ClassPathResource từ đường dẫn của tệp PDF
        Resource resource = new ClassPathResource(pdfFolderPath + filename);

        // Kiểm tra xem tệp có tồn tại không
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        // Chuyển đổi tên tệp thành dạng không Unicode (UTF-8)
        String sanitizedFilename = sanitizeFilename(filename);
        System.out.println(sanitizedFilename);

        // Thiết lập các tiêu đề và loại dữ liệu
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sanitizedFilename);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
    private String sanitizeFilename(String filename) {
        // Chuyển đổi ký tự Unicode thành dạng không Unicode (UTF-8)
        return new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }
}
