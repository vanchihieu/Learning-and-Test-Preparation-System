package com.backend.spring.service;

import com.backend.spring.entity.Topic;
import com.backend.spring.entity.Vocabulary;
import com.backend.spring.payload.request.VocabularyDto;
import com.backend.spring.repository.TopicRepository;
import com.backend.spring.repository.VocabularyRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class VocabularyService {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private TopicRepository topicRepository;


    @Transactional
    public void uploadVocabularyFromExcel(MultipartFile file, Integer topicId) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<Vocabulary> vocabularies = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                // Bỏ qua dòng tiêu đề (nếu có)
                continue;
            }

            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setWord(row.getCell(0).getStringCellValue());
            vocabulary.setIpa(row.getCell(1).getStringCellValue());
            vocabulary.setMeaning(row.getCell(2).getStringCellValue());
            vocabulary.setExampleSentence(row.getCell(3).getStringCellValue());

            // Sử dụng topicId từ tham số
            Topic topic = new Topic();
            topic.setTopicId(topicId);
            vocabulary.setTopic(topic);
            vocabulary.setVocabularyStatus(1);

            vocabularies.add(vocabulary);
        }

        vocabularyRepository.saveAll(vocabularies);
    }

    public List<Vocabulary> getAllVocabularies() {
        return vocabularyRepository.findAll();
    }

    public Vocabulary getVocabularyById(Integer id) {
        return vocabularyRepository.findById(id).orElse(null);
    }

    public void deleteVocabulary(Integer id) {
        vocabularyRepository.deleteById(id);
    }

    public Vocabulary createVocabulary(VocabularyDto vocabularyDto) throws IOException {
        MultipartFile image = vocabularyDto.getImage();
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Please select an image to upload.");
        }

        String imageName = image.getOriginalFilename();
        String imagePath = "images/vocabulary/";  // Thay đổi đường dẫn hình ảnh tương ứng
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path file = uploadPath.resolve(imageName);
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(image.getBytes());
        }

        Topic topic = topicRepository.findById(vocabularyDto.getTopicId()).orElse(null);

        if (topic != null) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setTopic(topic);
            vocabulary.setWord(vocabularyDto.getWord());
            vocabulary.setIpa(vocabularyDto.getIpa());
            vocabulary.setMeaning(vocabularyDto.getMeaning());
            vocabulary.setExampleSentence(vocabularyDto.getExampleSentence());
            vocabulary.setVocabularyStatus(vocabularyDto.getVocabularyStatus());
            vocabulary.setImage(imageName);

            return vocabularyRepository.save(vocabulary);
        } else {
            throw new IllegalArgumentException("Topic not found");
        }
    }

    public Vocabulary updateVocabulary(Integer id, VocabularyDto vocabularyDto) throws IOException {
        Optional<Vocabulary> vocabularyOptional = vocabularyRepository.findById(id);
        if (vocabularyOptional.isPresent()) {
            Vocabulary existingVocabulary = vocabularyOptional.get();

            MultipartFile image = vocabularyDto.getImage();
            if (image != null && !image.isEmpty()) {
                // Xóa ảnh cũ
                String oldImage = existingVocabulary.getImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImageFile = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", oldImage);
                    Files.deleteIfExists(oldImageFile);
                }
                // Lưu ảnh mới
                String imageName = image.getOriginalFilename();
                String imagePath = "images/vocabulary/";  // Thay đổi đường dẫn hình ảnh tương ứng
                Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path file = uploadPath.resolve(imageName);
                try (OutputStream os = Files.newOutputStream(file)) {
                    os.write(image.getBytes());
                }
                existingVocabulary.setImage(imageName);
            }

            existingVocabulary.setWord(vocabularyDto.getWord());
            existingVocabulary.setIpa(vocabularyDto.getIpa());
            existingVocabulary.setMeaning(vocabularyDto.getMeaning());
            existingVocabulary.setExampleSentence(vocabularyDto.getExampleSentence());
            existingVocabulary.setVocabularyStatus(vocabularyDto.getVocabularyStatus());
            existingVocabulary.setTopic(topicRepository.findById(vocabularyDto.getTopicId()).orElse(null));

            return vocabularyRepository.save(existingVocabulary);
        }
        return null;
    }

    @Transactional
    public void updateVocabularyStatus(Vocabulary vocabulary) {
        vocabulary.setUpdatedAt(LocalDateTime.now());
        vocabularyRepository.save(vocabulary);
    }

    public List<Vocabulary> getVocabulariesByTopicId(Integer topicId) {
        Optional<Topic> topicOptional = topicRepository.findById(topicId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            return vocabularyRepository.findByTopic(topic);
        }
        return null;
    }

}
