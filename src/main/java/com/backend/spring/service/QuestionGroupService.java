// QuestionGroupService.java
package com.backend.spring.service;

import com.backend.spring.entity.QuestionGroup;
import com.backend.spring.repository.QuestionGroupRepository;
import com.backend.spring.payload.request.QuestionGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class QuestionGroupService {

    @Autowired
    private QuestionGroupRepository questionGroupRepository;

    public QuestionGroup createQuestionGroup(QuestionGroupDto questionGroupDto) throws IOException {
        MultipartFile image = questionGroupDto.getGroupImage();
        MultipartFile audio = questionGroupDto.getGroupAudio();

        String imageName = null;
        String audioName = null;

        if (image != null && !image.isEmpty()) {
            imageName = image.getOriginalFilename();
            String imagePath = "images/";
            Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

            if (!Files.exists(uploadImagePath)) {
                Files.createDirectories(uploadImagePath);
            }

            Path imageFile = uploadImagePath.resolve(imageName);
            try (OutputStream osImage = Files.newOutputStream(imageFile)) {
                osImage.write(image.getBytes());
            }
        }

        if (audio != null && !audio.isEmpty()) {
            audioName = audio.getOriginalFilename();
            String audioPath = "audios/";
            Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", audioPath);

            if (!Files.exists(uploadAudioPath)) {
                Files.createDirectories(uploadAudioPath);
            }

            Path audioFile = uploadAudioPath.resolve(audioName);
            try (OutputStream osAudio = Files.newOutputStream(audioFile)) {
                osAudio.write(audio.getBytes());
            }
        }

        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setGroupImage(imageName); // Có thể là null
        questionGroup.setGroupScript(questionGroupDto.getGroupScript());
        questionGroup.setGroupAudio(audioName); // Có thể là null
        questionGroup.setGroupPassage(questionGroupDto.getGroupPassage());
        questionGroup.setGroupText(questionGroupDto.getGroupText());
        return questionGroupRepository.save(questionGroup);
    }

    public QuestionGroup updateQuestionGroup(Integer groupId, QuestionGroupDto questionGroupDto) throws IOException {
        Optional<QuestionGroup> groupOptional = questionGroupRepository.findById(groupId);
        if (groupOptional.isPresent()) {
            QuestionGroup existingGroup = groupOptional.get();
            existingGroup.setGroupScript(questionGroupDto.getGroupScript());
            existingGroup.setGroupPassage(questionGroupDto.getGroupPassage());
            existingGroup.setGroupText(questionGroupDto.getGroupText());
            MultipartFile image = questionGroupDto.getGroupImage();
            MultipartFile audio = questionGroupDto.getGroupAudio();

            if (image != null && !image.isEmpty()) {
                String imageName = image.getOriginalFilename();
                String imagePath = "images/";
                Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);

                if (!Files.exists(uploadImagePath)) {
                    Files.createDirectories(uploadImagePath);
                }

                // Xóa ảnh cũ nếu có
                String oldImage = existingGroup.getGroupImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImageFile = uploadImagePath.resolve(oldImage);
                    Files.deleteIfExists(oldImageFile);
                }
                // Lưu ảnh mới
                Path file = uploadImagePath.resolve(imageName);
                try (OutputStream os = Files.newOutputStream(file)) {
                    os.write(image.getBytes());
                }
                existingGroup.setGroupImage(imageName);
            }

            if (audio != null && !audio.isEmpty()) {
                String audioName = audio.getOriginalFilename();
                String audioPath = "audios/";
                Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", audioPath);

                if (!Files.exists(uploadAudioPath)) {
                    Files.createDirectories(uploadAudioPath);
                }

                // Xóa audio cũ nếu có
                String oldAudio = existingGroup.getGroupAudio();
                if (oldAudio != null && !oldAudio.isEmpty()) {
                    Path oldAudioFile = uploadAudioPath.resolve(oldAudio);
                    Files.deleteIfExists(oldAudioFile);
                }
                // Lưu audio mới
                Path file = uploadAudioPath.resolve(audioName);
                try (OutputStream os = Files.newOutputStream(file)) {
                    os.write(audio.getBytes());
                }
                existingGroup.setGroupAudio(audioName);
            }

            return questionGroupRepository.save(existingGroup);
        }
        return null;
    }

    public List<QuestionGroup> getAllQuestionGroups() {
        return questionGroupRepository.findAll();
    }

    public QuestionGroup getQuestionGroupById(Integer groupId) {
        return questionGroupRepository.findById(groupId).orElse(null);
    }

    public void deleteQuestionGroup(Integer groupId) {
        questionGroupRepository.deleteById(groupId);
    }
}
