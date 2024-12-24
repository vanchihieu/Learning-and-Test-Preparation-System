package com.backend.spring.service;
import com.backend.spring.entity.Topic;
import com.backend.spring.repository.TopicRepository;

import org.apache.poi.ss.usermodel.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.backend.spring.payload.request.TopicDto;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Transactional
    public void uploadTopicFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<Topic> topics = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                // Bỏ qua dòng tiêu đề (nếu có)
                continue;
            }
            Topic topic = new Topic();
            topic.setTopicName(row.getCell(0).getStringCellValue()); // Chỉnh index cột tương ứng trong file Excel
            topic.setTopicStatus(1);

            topics.add(topic);
        }

        topicRepository.saveAll(topics);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopicById(Integer id) {
        return topicRepository.findById(id).orElse(null);
    }

    public Topic createTopic(TopicDto topicDto) throws IOException {
        MultipartFile image = topicDto.getImage();
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

        Topic topic = new Topic();
        topic.setTopicName(topicDto.getTopicName());
        topic.setImage(imageName);
        topic.setTopicStatus(topicDto.getTopicStatus());
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());

        return topicRepository.save(topic);
    }

    public Topic updateTopic(Integer id, TopicDto topicDto) throws IOException {
        Optional<Topic> topicOptional = topicRepository.findById(id);
        if (topicOptional.isPresent()) {
            Topic existingTopic = topicOptional.get();
            existingTopic.setTopicName(topicDto.getTopicName());
            existingTopic.setTopicStatus(topicDto.getTopicStatus());
            existingTopic.setUpdatedAt(LocalDateTime.now());

            MultipartFile image = topicDto.getImage();
            if (image != null && !image.isEmpty()) {
                String imageName = image.getOriginalFilename();
                String imagePath = "images/";
                Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Xóa ảnh cũ
                String oldImage = existingTopic.getImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImageFile = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", oldImage);
                    Files.deleteIfExists(oldImageFile);
                }
                // Lưu ảnh mới
                Path file = uploadPath.resolve(imageName);
                try (OutputStream os = Files.newOutputStream(file)) {
                    os.write(image.getBytes());
                }
                existingTopic.setImage(imageName);
            }
            return topicRepository.save(existingTopic);
        }
        return null;
    }

    @Transactional
    public void updateTopicStatus(Topic topic) {
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);
    }
    public void deleteTopic(Integer id) {
        topicRepository.deleteById(id);
    }

    public boolean isTopicNameExists(String topicName) {
        return topicRepository.existsByTopicName(topicName);
    }

    public boolean isTopicNameExists(String topicName, Integer id) {
        return topicRepository.existsByTopicNameAndTopicIdNot(topicName, id);
    }
}
