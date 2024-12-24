package com.backend.spring.service;

import com.backend.spring.entity.ExamQuestion;
import com.backend.spring.entity.Exam;
import com.backend.spring.payload.request.ExamQuestionDto;
import com.backend.spring.repository.ExamQuestionRepository;
import com.backend.spring.repository.ExamRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
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
public class ExamQuestionService {

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamRepository examRepository;

    @Transactional
    public void uploadExamQuestionsFromExcel(MultipartFile file, Integer examId) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<ExamQuestion> examQuestions = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                // Bỏ qua dòng tiêu đề (nếu có)
                continue;
            }
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setQuestionContent(getStringValue(row.getCell(0)));
            examQuestion.setOptionA(getStringValue(row.getCell(1)));
            examQuestion.setOptionB(getStringValue(row.getCell(2)));
            examQuestion.setOptionC(getStringValue(row.getCell(3)));
            examQuestion.setOptionD(getStringValue(row.getCell(4)));
            examQuestion.setCorrectOption(getStringValue(row.getCell(5)));
            examQuestion.setQuestionImage(getStringValue(row.getCell(6)));
            examQuestion.setQuestionScript(getStringValue(row.getCell(7)));
            examQuestion.setQuestionAudio(getStringValue(row.getCell(8)));
            examQuestion.setQuestionExplanation(getStringValue(row.getCell(9)));
            examQuestion.setQuestionStatus(1);
            examQuestion.setOrderNumber((int) row.getCell(10).getNumericCellValue());
            examQuestion.setQuestionPassage(getStringValue(row.getCell(11)));
            examQuestion.setQuestionPart((int) row.getCell(12).getNumericCellValue());
            examQuestion.setQuestionType(getStringValue(row.getCell(13)));

            // Sử dụng examId từ tham số
            Exam exam = new Exam();
            exam.setExamId(examId);
            examQuestion.setExam(exam);

            examQuestions.add(examQuestion);
        }
        examQuestionRepository.saveAll(examQuestions);
    }

    private String getStringValue(Cell cell) {
        if (cell != null) {
            // Kiểm tra kiểu dữ liệu của ô Excel
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                // Xử lý giá trị kiểu số
                return String.valueOf((int) cell.getNumericCellValue());
            }
        }
        return ""; // Trả về giá trị rỗng nếu ô là null hoặc không có giá trị
    }

    public ExamQuestion createExamQuestion(ExamQuestionDto examQuestionDto) throws IOException {
        MultipartFile questionImage = examQuestionDto.getQuestionImage();
        MultipartFile questionAudio = examQuestionDto.getQuestionAudio();

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

        ExamQuestion examQuestion = new ExamQuestion();

        examQuestion.setQuestionContent(examQuestionDto.getQuestionContent());
        examQuestion.setOptionA(examQuestionDto.getOptionA());
        examQuestion.setOptionB(examQuestionDto.getOptionB());
        examQuestion.setOptionC(examQuestionDto.getOptionC());
        examQuestion.setOptionD(examQuestionDto.getOptionD());
        examQuestion.setCorrectOption(examQuestionDto.getCorrectOption());
        examQuestion.setQuestionType(examQuestionDto.getQuestionType());
        examQuestion.setQuestionImage(imageName); // Có thể là null
        examQuestion.setQuestionScript(examQuestionDto.getQuestionScript());
        examQuestion.setQuestionExplanation(examQuestionDto.getQuestionExplanation());
        examQuestion.setQuestionAudio(audioName); // Có thể là null
        examQuestion.setQuestionStatus(1);
        examQuestion.setCreatedAt(LocalDateTime.now());
        examQuestion.setUpdatedAt(LocalDateTime.now());

        // Lấy đối tượng Exam từ examId
        Exam exam = getExamById(examQuestionDto.getExamId());
        if (exam == null) {
            throw new IllegalArgumentException("Invalid examId: " + examQuestionDto.getExamId());
        }
        examQuestion.setExam(exam);
        examQuestion.setQuestionPart(examQuestionDto.getQuestionPart());

        return examQuestionRepository.save(examQuestion);
    }

    public ExamQuestion updateExamQuestion(Integer examQuestionId, ExamQuestionDto examQuestionDto) throws IOException {
        Optional<ExamQuestion> examQuestionOptional = examQuestionRepository.findById(examQuestionId);
        if (examQuestionOptional.isPresent()) {
            ExamQuestion existingExamQuestion = examQuestionOptional.get();
            existingExamQuestion.setQuestionContent(examQuestionDto.getQuestionContent());
            existingExamQuestion.setOptionA(examQuestionDto.getOptionA());
            existingExamQuestion.setOptionB(examQuestionDto.getOptionB());
            existingExamQuestion.setOptionC(examQuestionDto.getOptionC());
            existingExamQuestion.setOptionD(examQuestionDto.getOptionD());
            existingExamQuestion.setCorrectOption(examQuestionDto.getCorrectOption());
            existingExamQuestion.setQuestionType(examQuestionDto.getQuestionType());
            existingExamQuestion.setQuestionScript(examQuestionDto.getQuestionScript());
            existingExamQuestion.setQuestionExplanation(examQuestionDto.getQuestionExplanation());
            existingExamQuestion.setQuestionStatus(1);
            existingExamQuestion.setUpdatedAt(LocalDateTime.now());

            MultipartFile questionImage = examQuestionDto.getQuestionImage();
            MultipartFile questionAudio = examQuestionDto.getQuestionAudio();

            String imageName = existingExamQuestion.getQuestionImage(); // Lấy tên ảnh hiện tại
            String audioName = existingExamQuestion.getQuestionAudio(); // Lấy tên audio hiện tại

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
                String oldImage = existingExamQuestion.getQuestionImage();
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
                String oldAudio = existingExamQuestion.getQuestionAudio();
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

            existingExamQuestion.setQuestionImage(imageName);
            existingExamQuestion.setQuestionAudio(audioName);

            // Lấy đối tượng Exam từ examId
            Exam exam = getExamById(examQuestionDto.getExamId());
            if (exam == null) {
                throw new IllegalArgumentException("Invalid examId: " + examQuestionDto.getExamId());
            }
            existingExamQuestion.setExam(exam);
            existingExamQuestion.setQuestionPart(examQuestionDto.getQuestionPart());

            return examQuestionRepository.save(existingExamQuestion);
        }
        return null;
    }

    public ExamQuestion getExamQuestionById(Integer examQuestionId) {
        return examQuestionRepository.findById(examQuestionId).orElse(null);
    }

    public Exam getExamById(Integer examId) {
        return examRepository.findById(examId).orElse(null);
    }

    public void deleteExamQuestion(Integer examQuestionId) {
        examQuestionRepository.deleteById(examQuestionId);
    }

    public List<ExamQuestion> getAllExamQuestions() {
        return examQuestionRepository.findAll();
    }

    @Transactional
    public void updateExamQuestionStatus(ExamQuestion examQuestion) {
        examQuestion.setUpdatedAt(LocalDateTime.now());
        examQuestionRepository.save(examQuestion);
    }

    public List<ExamQuestion> getExamQuestionsByExamId(Integer examId) {
        Optional<Exam> examOptional = examRepository.findById(examId);
        if (examOptional.isPresent()) {
            Exam exam = examOptional.get();
            return examQuestionRepository.findByExam(exam);
        }
        return null;
    }

    @Transactional
    public void deleteExamQuestionsByExamId(Integer examId) {
        // Lấy danh sách câu hỏi dựa vào examId
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam_ExamId(examId);
        // Xóa tất cả câu hỏi trong danh sách
        examQuestionRepository.deleteAll(examQuestions);
    }

}
