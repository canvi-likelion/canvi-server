package com.canvi.hama.domain.diary.controller;

import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.entity.Image;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.repository.CommentRepository;
import com.canvi.hama.domain.diary.repository.DiaryRepository;
import com.canvi.hama.domain.diary.repository.ImageRepository;
import com.canvi.hama.domain.diary.request.CommentSaveRequest;
import com.canvi.hama.domain.diary.request.DiaryRequest;
import com.canvi.hama.domain.diary.request.ImageSaveRequest;
import com.canvi.hama.domain.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.diary.service.DiaryService;
import com.canvi.hama.domain.diary.swagger.comment.GetCommentApi;
import com.canvi.hama.domain.diary.swagger.comment.SaveCommentApi;
import com.canvi.hama.domain.diary.swagger.diary.GetDiaryApi;
import com.canvi.hama.domain.diary.swagger.diary.SaveDiaryApi;
import com.canvi.hama.domain.diary.swagger.image.GetImageApi;
import com.canvi.hama.domain.diary.swagger.image.SaveImageApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Diary")
@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final ImageRepository imageRepository;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;

    @SaveDiaryApi
    @PostMapping("/save")
    public ResponseEntity<DiaryResponseStatus> saveDiary(@RequestBody DiaryRequest diaryRequest) {
        diaryService.saveDiary(diaryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetDiaryApi
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getDiariesByUserId(@PathVariable Long userId) {
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }

    @SaveCommentApi
    @PostMapping("/comment/save")
    public ResponseEntity<DiaryResponseStatus> saveComment(@RequestBody CommentSaveRequest commentSaveRequest) {
        diaryService.saveComment(commentSaveRequest.getDiaryId(), commentSaveRequest.getUserId(), commentSaveRequest.getComment());

        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetCommentApi
    @GetMapping("/comment/{diaryId}")
    public ResponseEntity<?> getComment(@PathVariable Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        Comment comments = commentRepository.findByDiaryId(diary).orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        return ResponseEntity.ok(comments);
    }

    @SaveImageApi
    @PostMapping("/image/save")
    public ResponseEntity<DiaryResponseStatus> saveImage(@RequestBody ImageSaveRequest imageSaveRequest) {

        diaryService.saveImageFromUrl(imageSaveRequest.getDiaryId(), imageSaveRequest.getImageUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetImageApi
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
