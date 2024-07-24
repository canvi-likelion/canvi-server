package com.canvi.hama.diary.service;

import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.diary.repository.DiaryRepository;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.user.domain.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }

    public void saveDiary(DiaryRequest diaryRequest) {

        User user = getUserByUserId(diaryRequest.getUserId());

        Diary diary = Diary.builder()
                .user(user)
                .title(diaryRequest.getTitle())
                .content(diaryRequest.getContent())
                .diaryDate(diaryRequest.getDiaryDate())
                .build();

        diaryRepository.save(diary);
    }

    public List<Diary> getDiariesByUserId(Long userId) {
        return diaryRepository.findByUserId(userId);
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
    }
}
