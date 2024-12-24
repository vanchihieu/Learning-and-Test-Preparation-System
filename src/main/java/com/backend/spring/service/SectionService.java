package com.backend.spring.service;

import com.backend.spring.entity.Section;
import com.backend.spring.repository.SectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.backend.spring.payload.request.SectionDto;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;
    @Transactional
    public Section createSection(SectionDto sectionDto) throws IOException {
        MultipartFile image = sectionDto.getImage();
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Please select an image to upload.");
        }

        String imageName = image.getOriginalFilename();
        String imagePath = "images/";
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path file = uploadPath.resolve(imageName);
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(image.getBytes());
        }

        Section section = new Section();
        section.setName(sectionDto.getName());
        section.setDescription(sectionDto.getDescription()); // Thêm trường description
        section.setStatus(section.getStatus());
        section.setType(sectionDto.getType());
        section.setImage(imageName);
        section.setCreatedAt(LocalDateTime.now());
        section.setUpdatedAt(LocalDateTime.now());
        return sectionRepository.save(section);

    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section getSectionById(Integer id) {
        return sectionRepository.findById(id).orElse(null);
    }

    public Section updateSection(Integer id, SectionDto sectionDto) throws IOException {
        Optional<Section> sectionOptional = sectionRepository.findById(id);
        if (sectionOptional.isPresent()) {
            Section existingSection = sectionOptional.get();
            existingSection.setName(sectionDto.getName());
            existingSection.setDescription(sectionDto.getDescription()); // Thêm trường description
            existingSection.setStatus(sectionDto.getStatus());
            existingSection.setType(sectionDto.getType());

            MultipartFile image = sectionDto.getImage();
            if (image != null && !image.isEmpty()) {
                String imageName = image.getOriginalFilename();
                String imagePath = "images/";
                Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Xóa ảnh cũ
                String oldImage = existingSection.getImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImageFile = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", oldImage);
                    Files.deleteIfExists(oldImageFile);
                }
                // Lưu ảnh mới
                Path file = uploadPath.resolve(imageName);
                try (OutputStream os = Files.newOutputStream(file)) {
                    os.write(image.getBytes());
                }
                existingSection.setImage(imageName);
            }
            existingSection.setUpdatedAt(LocalDateTime.now());
            return sectionRepository.save(existingSection);
        }
        return null;
    }

    @Transactional
    public void updateSectionStatus(Section section) {
        section.setUpdatedAt(LocalDateTime.now());
        sectionRepository.save(section);
    }

    public void deleteSection(Integer id) {
        sectionRepository.deleteById(id);
    }

    public String getSectionNameById(Integer id) {
        Optional<Section> sectionOptional = sectionRepository.findById(id);
        return sectionOptional.map(Section::getName).orElse(null);
    }

    public boolean isSectionNameExists(String name) {
        return sectionRepository.existsByName(name);
    }

    public boolean isSectionNameExists(String name, Integer id) {
        return sectionRepository.existsByNameAndIdNot(name, id);
    }

}
