package com.canvi.hama.diary.service;

import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.repository.DiaryRepository;
import com.canvi.hama.diary.request.DiaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public void saveDiary(DiaryRequest diaryRequest) {

        Diary diary = Diary.builder()
                .userId(diaryRequest.getUserId())
                .title(diaryRequest.getTitle())
                .content(diaryRequest.getContent())
                .diaryDate(diaryRequest.getDiaryDate())
                .build();

        diaryRepository.save(diary);
    }

    public List<Diary> getDiariesByUserId(Integer userId) {
        return diaryRepository.findByUserId(userId);
    }
}
