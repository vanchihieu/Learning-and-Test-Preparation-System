package com.backend.spring.service;

import com.backend.spring.entity.*;
import com.backend.spring.payload.request.UserVocabularyDto;
import com.backend.spring.repository.VocabularyRepository;
import com.backend.spring.repository.UserVocabularyRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserVocabularyService {

    @Autowired
    private UserVocabularyRepository userVocabularyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Transactional
    public UserVocabulary createUserVocabulary(UserVocabularyDto userVocabularyDto) {
        Optional<User> userOptional = userRepository.findById(userVocabularyDto.getUserId());
        Optional<Vocabulary> vocabularyOptional = vocabularyRepository.findById(Math.toIntExact(userVocabularyDto.getVocabularyId()));

        if (userOptional.isPresent() && vocabularyOptional.isPresent()) {
            User user = userOptional.get();
            Vocabulary vocabulary = vocabularyOptional.get();

            UserVocabulary userVocabulary = new UserVocabulary();
            userVocabulary.setUser(user);
            userVocabulary.setVocabulary(vocabulary);
            return userVocabularyRepository.save(userVocabulary);
        }
        return null;
    }

    @Transactional
    public List<UserVocabulary> getUserVocabulariesByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return userVocabularyRepository.findByUser(user);
        }
        return null;
    }

    @Transactional
    public void deleteUserVocabularyByVocabularyIdAndUserId(Long vocabularyId, Long userId) {
        userVocabularyRepository.deleteByVocabulary_VocabularyIdAndUser_Id(vocabularyId, userId);
    }


}

