package com.canvi.hama.diary.controller;

import com.canvi.hama.diary.entity.Comment;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.entity.Image;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.diary.repository.CommentRepository;
import com.canvi.hama.diary.repository.ImageRepository;
import com.canvi.hama.diary.request.CommentSaveRequest;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.request.ImageSaveRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import com.canvi.hama.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public DiaryController(DiaryService diaryService, ImageRepository imageRepository, CommentRepository commentRepository) {
        this.diaryService = diaryService;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveDiary(@RequestBody DiaryRequest diaryRequest) {
        diaryService.saveDiary(diaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("일기 저장 완료");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getDiariesByUserId(@PathVariable Long userId) {
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }

    @PostMapping("/comment/save")
    public ResponseEntity<?> saveComment(@RequestBody CommentSaveRequest commentSaveRequest) {
        diaryService.saveComment(commentSaveRequest.getDiaryId(), commentSaveRequest.getUserId(), commentSaveRequest.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body("comment 저장 성공");
    }

    @GetMapping("comment/{diaryId}")
    public ResponseEntity<?> getComment(@PathVariable Long diaryId) {
        Comment comment = commentRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/image/save")
    public ResponseEntity<?> saveImage(@RequestBody ImageSaveRequest imageSaveRequest) {

        diaryService.saveImageFromUrl(imageSaveRequest.getDiaryId(), imageSaveRequest.getImageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body("이미지 저장 완료");
    }

    @GetMapping("/image/{diaryId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long diaryId) {
        Image image = imageRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        try {
            File imgFile = new File(image.getUrl());
            byte[] imageBytes = Files.readAllBytes(imgFile.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
