package com.backend.spring.service;

import com.backend.spring.entity.Question;
import com.backend.spring.entity.QuestionGroup;
import com.backend.spring.entity.Section;
import com.backend.spring.entity.Test;
import com.backend.spring.payload.request.QuestionDto;
import com.backend.spring.repository.QuestionGroupRepository;
import com.backend.spring.repository.QuestionRepository;
import com.backend.spring.repository.SectionRepository;
import com.backend.spring.repository.TestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionGroupRepository questionGroupRepository;

    public Question createQuestion(QuestionDto questionDto) throws IOException {
        MultipartFile questionImage = questionDto.getQuestionImage();
        MultipartFile questionAudio = questionDto.getQuestionAudio();

        String imageName = null;
        String audioName = null;
        String imagePath = "images/";
        String audioPath = "audios/";
        Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);
        Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", audioPath);

        if (questionImage != null && !questionImage.isEmpty()) {
            imageName = questionImage.getOriginalFilename();
            if (!Files.exists(uploadImagePath)) {
                Files.createDirectories(uploadImagePath);
            }
            Path imageFile = uploadImagePath.resolve(imageName);
            try (OutputStream osImage = Files.newOutputStream(imageFile)) {
                osImage.write(questionImage.getBytes());
            }
        }

        if (questionAudio != null && !questionAudio.isEmpty()) {
            audioName = questionAudio.getOriginalFilename();
            if (!Files.exists(uploadAudioPath)) {
                Files.createDirectories(uploadAudioPath);
            }
            Path audioFile = uploadAudioPath.resolve(audioName);
            try (OutputStream osAudio = Files.newOutputStream(audioFile)) {
                osAudio.write(questionAudio.getBytes());
            }
        }

        Question question = new Question();

        question.setQuestionContent(questionDto.getQuestionContent());
        question.setOptionA(questionDto.getOptionA());
        question.setOptionB(questionDto.getOptionB());
        question.setOptionC(questionDto.getOptionC());
        question.setOptionD(questionDto.getOptionD());
        question.setCorrectOption(questionDto.getCorrectOption());
        question.setQuestionType(questionDto.getQuestionType());
        question.setQuestionImage(imageName); // Có thể là null
        question.setQuestionScript(questionDto.getQuestionScript());
        question.setQuestionExplanation(questionDto.getQuestionExplanation());
        question.setQuestionAudio(audioName); // Có thể là null
        question.setQuestionPassage(questionDto.getQuestionPassage());
        question.setQuestionText(questionDto.getQuestionText());
        question.setSuggestedAnswer(questionDto.getSuggestedAnswer());
        question.setQuestionStatus(1);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        // Lấy đối tượng Test từ testId
        Section section = getSectionById(questionDto.getSectionId());
        if (section == null) {
            throw new IllegalArgumentException("Invalid testId: " + questionDto.getSectionId());
        }
        question.setSection(section);

        // Lấy đối tượng QuestionGroup từ groupId (Nếu groupId không null)
        if (questionDto.getGroupId() != null) {
            QuestionGroup questionGroup = getQuestionGroupById(questionDto.getGroupId());
            if (questionGroup == null) {
                throw new IllegalArgumentException("Invalid groupId: " + questionDto.getGroupId());
            }
            question.setQuestionGroup(questionGroup);
        }

        return questionRepository.save(question);
    }

    public Question updateQuestion(Integer questionId, QuestionDto questionDto) throws IOException {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isPresent()) {
            Question existingQuestion = questionOptional.get();
            existingQuestion.setQuestionContent(questionDto.getQuestionContent());
            existingQuestion.setOptionA(questionDto.getOptionA());
            existingQuestion.setOptionB(questionDto.getOptionB());
            existingQuestion.setOptionC(questionDto.getOptionC());
            existingQuestion.setOptionD(questionDto.getOptionD());
            existingQuestion.setCorrectOption(questionDto.getCorrectOption());
            existingQuestion.setQuestionType(questionDto.getQuestionType());
            existingQuestion.setQuestionScript(questionDto.getQuestionScript());
            existingQuestion.setQuestionExplanation(questionDto.getQuestionExplanation());
            existingQuestion.setQuestionPassage(questionDto.getQuestionPassage());
            existingQuestion.setQuestionText(questionDto.getQuestionText());
            existingQuestion.setSuggestedAnswer(questionDto.getSuggestedAnswer());
            existingQuestion.setQuestionStatus(1);
            existingQuestion.setUpdatedAt(LocalDateTime.now());

            MultipartFile questionImage = questionDto.getQuestionImage();
            MultipartFile questionAudio = questionDto.getQuestionAudio();

            String imageName = existingQuestion.getQuestionImage(); // Lấy tên ảnh hiện tại
            String audioName = existingQuestion.getQuestionAudio(); // Lấy tên audio hiện tại

            String imagePath = "images/";
            String audioPath = "audios/";

            Path uploadImagePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);
            Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", audioPath);

            if (questionImage != null && !questionImage.isEmpty()) {
                // Nếu có ảnh mới được upload, xử lý ảnh và cập nhật tên mới
                imageName = questionImage.getOriginalFilename();

                if (!Files.exists(uploadImagePath)) {
                    Files.createDirectories(uploadImagePath);
                }

                // Xóa ảnh cũ nếu có
                String oldImage = existingQuestion.getQuestionImage();
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImageFile = uploadImagePath.resolve(oldImage);
                    Files.deleteIfExists(oldImageFile);
                }

                // Lưu ảnh mới
                Path imageFile = uploadImagePath.resolve(imageName);
                try (OutputStream osImage = Files.newOutputStream(imageFile)) {
                    osImage.write(questionImage.getBytes());
                }
            }

            if (questionAudio != null && !questionAudio.isEmpty()) {
                // Nếu có audio mới được upload, xử lý audio và cập nhật tên mới
                audioName = questionAudio.getOriginalFilename();

                if (!Files.exists(uploadAudioPath)) {
                    Files.createDirectories(uploadAudioPath);
                }

                // Xóa audio cũ nếu có
                String oldAudio = existingQuestion.getQuestionAudio();
                if (oldAudio != null && !oldAudio.isEmpty()) {
                    Path oldAudioFile = uploadAudioPath.resolve(oldAudio);
                    Files.deleteIfExists(oldAudioFile);
                }

                // Lưu audio mới
                Path audioFile = uploadAudioPath.resolve(audioName);
                try (OutputStream osAudio = Files.newOutputStream(audioFile)) {
                    osAudio.write(questionAudio.getBytes());
                }
            }

            existingQuestion.setQuestionImage(imageName);
            existingQuestion.setQuestionAudio(audioName);

            // Lấy đối tượng Test từ testId
            Section section = getSectionById(questionDto.getSectionId());
            if (section == null) {
                throw new IllegalArgumentException("Invalid testId: " + questionDto.getSectionId());
            }
            existingQuestion.setSection(section);

            // Lấy đối tượng QuestionGroup từ groupId (Nếu groupId không null)
            if (questionDto.getGroupId() != null) {
                QuestionGroup questionGroup = getQuestionGroupById(questionDto.getGroupId());
                if (questionGroup == null) {
                    throw new IllegalArgumentException("Invalid groupId: " + questionDto.getGroupId());
                }
                existingQuestion.setQuestionGroup(questionGroup);
            }

            return questionRepository.save(existingQuestion);
        }
        return null;
    }


    @Transactional
    public void updateQuestionStatus(Question question) {
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Integer questionId) {
        return questionRepository.findById(questionId).orElse(null);
    }

    public QuestionGroup getQuestionGroupById(Integer groupId) {
        return questionGroupRepository.findById(groupId).orElse(null);
    }
    public Section getSectionById(Integer sectionId) {
        return sectionRepository.findById(sectionId).orElse(null);
    }

    public void deleteQuestion(Integer questionId) {
        questionRepository.deleteById(questionId);
    }

    public List<Question> getQuestionsBySectionId(Integer sectionId) {
        Optional<Section> sectionOptional = sectionRepository.findById(sectionId);
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            List<Question> questionList = questionRepository.findBySection(section);

            // Sắp xếp câu hỏi theo trường "questionId"
            questionList.sort(Comparator.comparing(Question::getQuestionId));

            return questionList;
        }
        return Collections.emptyList();
    }

    public long countQuestionUsage(Integer questionId) {
        long count = 0;
        for (Test test : testRepository.findAll()) {
            if (test.getQuestions().stream().anyMatch(question -> question.getQuestionId().equals(questionId))) {
                count++;
            }
        }
        return count;
    }

    public List<Question> getQuestionsByGroupId(Integer groupId) {
        Optional<QuestionGroup> questionGroupOptional = questionGroupRepository.findById(groupId);
        if (questionGroupOptional.isPresent()) {
            QuestionGroup questionGroup = questionGroupOptional.get();
            return questionRepository.findByQuestionGroup(questionGroup);
        }
        return null;
    }

    public List<Question> getQuestionsBySectionIdAndType(Integer sectionId, String questionType) {
        // Thực hiện truy vấn cơ sở dữ liệu để lấy danh sách câu hỏi dựa trên sectionId và questionType
        return questionRepository.findBySectionIdAndQuestionType(sectionId, questionType);
    }


}
