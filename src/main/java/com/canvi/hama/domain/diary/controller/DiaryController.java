package com.canvi.hama.domain.diary.controller;

import com.canvi.hama.domain.diary.dto.request.CommentSaveRequest;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.dto.request.ImageSaveRequest;
import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.enums.DiaryResponseStatus;
import com.canvi.hama.domain.diary.service.DiaryService;
import com.canvi.hama.domain.diary.swagger.comment.GetCommentApi;
import com.canvi.hama.domain.diary.swagger.comment.SaveCommentApi;
import com.canvi.hama.domain.diary.swagger.diary.GetDiaryApi;
import com.canvi.hama.domain.diary.swagger.diary.SaveDiaryApi;
import com.canvi.hama.domain.diary.swagger.image.GetImageApi;
import com.canvi.hama.domain.diary.swagger.image.SaveImageApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Diary")
@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @SaveDiaryApi
    @PostMapping
    public ResponseEntity<DiaryResponseStatus> saveDiary(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody @Valid DiaryRequest diaryRequest) {
        diaryService.saveDiary(userDetails.getUsername(), diaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetDiaryApi
    @GetMapping
    public ResponseEntity<List<Diary>> getDiariesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<Diary> diaries = diaryService.getDiariesByUsername(userDetails.getUsername());
        return ResponseEntity.ok(diaries);
    }

    @SaveCommentApi
    @PostMapping("/{diaryId}/comments")
    public ResponseEntity<DiaryResponseStatus> addComment(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId,
                                                          @RequestBody @Valid CommentSaveRequest commentSaveRequest) {
        diaryService.saveComment(diaryId, commentSaveRequest.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetCommentApi
    @GetMapping("/{diaryId}/comments")
    public ResponseEntity<Comment> getDiaryComments(
            @PathVariable @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId) {
        Comment comments = diaryService.getCommentByDiaryId(diaryId);
        return ResponseEntity.ok(comments);
    }

    @SaveImageApi
    @PostMapping("/{diaryId}/images")
    public ResponseEntity<DiaryResponseStatus> addImage(
            @PathVariable @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId,
            @RequestBody @Valid ImageSaveRequest imageSaveRequest) {

        diaryService.saveImageFromUrl(diaryId, imageSaveRequest.imageUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetImageApi
    @GetMapping("/{diaryId}/images")
    public ResponseEntity<Resource> getDiaryImage(
            @PathVariable @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId) {
        Resource image = diaryService.getImageByDiaryId(diaryId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(image);
    }
}