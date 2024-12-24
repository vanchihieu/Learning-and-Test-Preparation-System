package com.backend.spring.service;

import com.backend.spring.entity.Grammar;
import com.backend.spring.entity.GrammarContent;
import com.backend.spring.payload.request.GrammarContentDto;
import com.backend.spring.repository.GrammarContentRepository;
import com.backend.spring.repository.GrammarRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class GrammarContentService {

    @Autowired
    private GrammarContentRepository grammarContentRepository;

    @Autowired
    private GrammarRepository grammarRepository;

    @Transactional
    public void uploadGrammarContentFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<GrammarContent> grammarContents = new ArrayList<>();
        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                // Bỏ qua dòng tiêu đề (nếu có)
                continue;
            }

            GrammarContent grammarContent = new GrammarContent();
            grammarContent.setTitle(row.getCell(0).getStringCellValue());
            grammarContent.setContent(row.getCell(1).getStringCellValue());

            // Lấy grammar_id từ cột số 2 (chỉnh index cột tương ứng trong file Excel)
            Integer grammarId = (int) row.getCell(3).getNumericCellValue();
            Grammar grammar = new Grammar();
            grammar.setGrammarId(grammarId);
            grammarContent.setGrammar(grammar);

            grammarContent.setGrammarContentStatus(1);

            grammarContents.add(grammarContent);
        }

        grammarContentRepository.saveAll(grammarContents);
    }

    @Transactional
    public GrammarContent createGrammarContent(GrammarContentDto grammarContentDto) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarContentDto.getGrammarId());
        if (grammarOptional.isPresent()) {
            Grammar grammar = grammarOptional.get();
            GrammarContent grammarContent = new GrammarContent();

            grammarContent.setGrammar(grammar);
            grammarContent.setTitle(grammarContentDto.getTitle());
            grammarContent.setContent(grammarContentDto.getContent());
            grammarContent.setGrammarContentStatus(grammarContentDto.getGrammarContentStatus());

            grammarContent.setCreatedAt(LocalDateTime.now());
            grammarContent.setUpdatedAt(LocalDateTime.now());

            return grammarContentRepository.save(grammarContent);
        }
        return null;
    }

    public GrammarContent updateGrammarContent(Integer id, GrammarContentDto grammarContentDto) {
        Optional<GrammarContent> grammarContentOptional = grammarContentRepository.findById(id);
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarContentDto.getGrammarId());
        if (grammarContentOptional.isPresent() && grammarOptional.isPresent()) {

            GrammarContent grammarContent = grammarContentOptional.get();
            Grammar grammar = grammarOptional.get();

            grammarContent.setGrammar(grammar);
            grammarContent.setTitle(grammarContentDto.getTitle());
            grammarContent.setContent(grammarContentDto.getContent());
            grammarContent.setGrammarContentStatus(grammarContentDto.getGrammarContentStatus());

            grammarContent.setUpdatedAt(LocalDateTime.now());

            return grammarContentRepository.save(grammarContent);
        }
        return null;
    }

    @Transactional
    public void updateGrammarContentStatus(GrammarContent grammarContent) {
        grammarContent.setUpdatedAt(LocalDateTime.now());
        grammarContentRepository.save(grammarContent);
    }

    public List<GrammarContent> getAllGrammarContents() {
        return grammarContentRepository.findAll();
    }

    public GrammarContent getGrammarContentById(Integer id) {
        return grammarContentRepository.findById(id).orElse(null);
    }


    public void deleteGrammarContent(Integer id) {
        grammarContentRepository.deleteById(id);
    }

    public List<GrammarContent> getGrammarContentsByGrammarId(Integer grammarId) {
        Optional<Grammar> grammarOptional = grammarRepository.findById(grammarId);
        if (grammarOptional.isPresent()) {
            Grammar grammar = grammarOptional.get();
            return grammarContentRepository.findByGrammar(grammar);
        }
        return null;
    }
}
