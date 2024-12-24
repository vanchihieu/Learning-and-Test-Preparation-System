package com.backend.spring.service;

import com.backend.spring.entity.FreeMaterial;
import com.backend.spring.repository.FreeMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.spring.payload.request.FreeMaterialDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FreeMaterialService {

    @Autowired
    private FreeMaterialRepository freeMaterialRepository;

    public List<FreeMaterial> getAllFreeMaterials() {
        return freeMaterialRepository.findAll();
    }

    public FreeMaterial getFreeMaterialById(Integer id) {
        return freeMaterialRepository.findById(id).orElse(null);
    }

    public FreeMaterial createFreeMaterial(FreeMaterialDto freeMaterialDto) throws IOException {
        System.out.println(freeMaterialDto);
        MultipartFile filePdf = freeMaterialDto.getFilePdf();
        if (filePdf == null || filePdf.isEmpty()) {
            throw new IllegalArgumentException("Please select a PDF file to upload.");
        }

        String pdfName = filePdf.getOriginalFilename();
        String pdfPath = "pdfs/";
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", pdfPath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path file = uploadPath.resolve(pdfName);
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(filePdf.getBytes());
        }

        FreeMaterial freeMaterial = new FreeMaterial();
        freeMaterial.setTitle(freeMaterialDto.getTitle());
        freeMaterial.setDescription(freeMaterialDto.getDescription());
        freeMaterial.setFilePdf(pdfName);
        freeMaterial.setMaterialStatus(freeMaterialDto.getMaterialStatus());
        freeMaterial.setCreatedAt(LocalDateTime.now());
        freeMaterial.setUpdatedAt(LocalDateTime.now());

        return freeMaterialRepository.save(freeMaterial);
    }

    public FreeMaterial updateFreeMaterial(Integer id, FreeMaterialDto freeMaterialDto) throws IOException {
        System.out.println(freeMaterialDto);
        Optional<FreeMaterial> freeMaterialOptional = freeMaterialRepository.findById(id);
        if (freeMaterialOptional.isPresent()) {
            FreeMaterial existingFreeMaterial = freeMaterialOptional.get();
            existingFreeMaterial.setTitle(freeMaterialDto.getTitle());
            existingFreeMaterial.setDescription(freeMaterialDto.getDescription());
            existingFreeMaterial.setMaterialStatus(freeMaterialDto.getMaterialStatus());
            existingFreeMaterial.setUpdatedAt(LocalDateTime.now());

            MultipartFile filePdf = freeMaterialDto.getFilePdf();
            if (filePdf != null && !filePdf.isEmpty()) {
                String pdfName = filePdf.getOriginalFilename();
                String pdfPath = "pdfs/";
                Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", pdfPath);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Xóa PDF cũ
                String oldPdf = existingFreeMaterial.getFilePdf();
                if (oldPdf != null && !oldPdf.isEmpty()) {
                    Path oldPdfFile = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", oldPdf);
                    Files.deleteIfExists(oldPdfFile);
                }
                // Lưu PDF mới
                Path pdfFile = uploadPath.resolve(pdfName);
                try (OutputStream os = Files.newOutputStream(pdfFile)) {
                    os.write(filePdf.getBytes());
                }
                existingFreeMaterial.setFilePdf(pdfName);
            }
            return freeMaterialRepository.save(existingFreeMaterial);
        }
        return null;
    }

    @Transactional
    public void updateFreeMaterialStatus(FreeMaterial freeMaterial) {
        freeMaterial.setUpdatedAt(LocalDateTime.now());
        freeMaterialRepository.save(freeMaterial);
    }

    public void deleteFreeMaterial(Integer id) {
        freeMaterialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countTotalFreeMaterials() {
        return freeMaterialRepository.count();
    }

}
