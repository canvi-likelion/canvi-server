package com.canvi.hama.diary.controller;

import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveDiary(@RequestBody DiaryRequest diaryRequest) {

        diaryService.saveDiary(diaryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("일기 저장 완료");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getDiariesByUserId(@PathVariable Integer userId) {
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }


}
