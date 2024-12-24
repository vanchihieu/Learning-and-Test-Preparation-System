package com.backend.spring.controller;

import com.backend.spring.entity.Section;
import com.backend.spring.payload.request.SectionDto;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/section")
public class SectionController {

    @Autowired
    private SectionService sectionService;

//  Admin
    @GetMapping
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sectionList = sectionService.getAllSections();
        return new ResponseEntity<>(sectionList, HttpStatus.OK);
    }

//  Người dùng
    @GetMapping("/enable")
    public ResponseEntity<List<Section>> getAllEnableSections() {
        List<Section> sectionList = sectionService.getAllSections();
        // Lọc danh sách chỉ giữ lại các section có status là 1
        List<Section> filteredSectionList = sectionList
                .stream()
                .filter(section -> section.getStatus() == 1)
                .collect(Collectors.toList());
        return new ResponseEntity<>(filteredSectionList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Section> getSectionById(@PathVariable Integer id) {
        Section section = sectionService.getSectionById(id);
        if (section != null) {
            return new ResponseEntity<>(section, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getSectionNameById(@PathVariable Integer id) {
        String sectionName = sectionService.getSectionNameById(id);
        if (sectionName != null) {
            return new ResponseEntity<>(sectionName, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createSection(@ModelAttribute SectionDto sectionDto) {
        try {
            // Kiểm tra xem tên section đã tồn tại chưa
            if (sectionService.isSectionNameExists(sectionDto.getName())) {
                return new ResponseEntity<>(new MessageResponse("Tên dạng phần đã tồn tại"), HttpStatus.BAD_REQUEST);
            }

            Section createdSection = sectionService.createSection(sectionDto);
            return ResponseEntity.ok(new MessageResponse("Thêm dạng phần thành công!"));
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi upload ảnh: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateSection(@PathVariable Integer id, @ModelAttribute SectionDto sectionDto) {
        try {
            // Kiểm tra trùng lặp tên section (nếu tên đã thay đổi)
            if (sectionService.isSectionNameExists(sectionDto.getName(), id)) {
                return new ResponseEntity<>(new MessageResponse("Tên dạng phần đã tồn tại"), HttpStatus.BAD_REQUEST);
            }

            Section updatedSection = sectionService.updateSection(id, sectionDto);
            if (updatedSection != null) {
                return ResponseEntity.ok(new MessageResponse("Cập nhật dạng phần thành công!!!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật dạng phần: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteSection(@PathVariable Integer id) {
        sectionService.deleteSection(id);
        return ResponseEntity.ok(new MessageResponse("Xóa dạng phần thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<MessageResponse> updateSectionStatus(@PathVariable Integer id, @RequestBody Integer newStatus) {
        try {
            System.out.println(newStatus);
            Section section = sectionService.getSectionById(id);
            if (section != null) {
                section.setStatus(newStatus);
                sectionService.updateSectionStatus(section);
                return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái dạng phần thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
