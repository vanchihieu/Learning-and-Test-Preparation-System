package com.backend.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        // Thư mục chứa tài liệu tĩnh
        String imagePath = "images/";

        // Đường dẫn tới thư mục tĩnh
        Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

        // Lấy tài liệu từ thư mục tĩnh
        Resource resource = resourceLoader.getResource("classpath:static/" + imagePath + imageName);
        InputStream inputStream = resource.getInputStream();

        // Thiết lập header Cache-Control để tránh cache và loại phương tiện là hình ảnh
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        // Thêm một tham số ngẫu nhiên vào URL
        imageName = imageName + "?" + System.currentTimeMillis(); // Thêm timestamp ngẫu nhiên

        // Trả về tài liệu dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(new ByteArrayResource(inputStream.readAllBytes()));
    }

}
