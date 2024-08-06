package com.canvi.hama.domain.diary.controller;

import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.domain.diary.dto.request.CommentSaveRequest;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.dto.request.ImageSaveRequest;
import com.canvi.hama.domain.diary.dto.response.CommentGetResponse;
import com.canvi.hama.domain.diary.dto.response.DiaryGetListResponse;
import com.canvi.hama.domain.diary.dto.response.DiaryGetResponse;
import com.canvi.hama.domain.diary.dto.response.SaveDiaryResponse;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @SaveDiaryApi
    @PostMapping
    public BaseResponse<SaveDiaryResponse> saveDiary(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody @Valid DiaryRequest diaryRequest) {
        return new BaseResponse<>(diaryService.saveDiary(userDetails, diaryRequest));
    }

    @GetDiaryApi
    @GetMapping
    public BaseResponse<DiaryGetListResponse> getDiariesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return new BaseResponse<>(diaryService.getDiariesByUsername(userDetails));
    }

    @GetDiaryApi
    @GetMapping("/{date}")
    public BaseResponse<DiaryGetResponse> getDiaryByDate(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("date")String dateStr) {
        return new BaseResponse<>(diaryService.getDiaryByDate(userDetails, dateStr));
    }


    @SaveCommentApi
    @PostMapping("/{diaryId}/comments")
    public ResponseEntity<DiaryResponseStatus> addComment(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable("diaryId") @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId,
                                                          @RequestBody @Valid CommentSaveRequest commentSaveRequest) {
        diaryService.saveComment(diaryId, commentSaveRequest.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetCommentApi
    @GetMapping("/{diaryId}/comments")
    public BaseResponse<CommentGetResponse> getDiaryComments(
            @PathVariable("diaryId") @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId) {
        return new BaseResponse<>(diaryService.getCommentByDiaryId(diaryId));
    }

    @SaveImageApi
    @PostMapping("/{diaryId}/images")
    public ResponseEntity<DiaryResponseStatus> addImage(
            @PathVariable("diaryId") @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId,
            @RequestBody @Valid ImageSaveRequest imageSaveRequest) {

        System.out.println("debug 1");
        diaryService.saveImageFromUrl(diaryId, imageSaveRequest.imageUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(DiaryResponseStatus.CREATED);
    }

    @GetImageApi
    @GetMapping("/{diaryId}/images")
    public ResponseEntity<Resource> getDiaryImage(
            @PathVariable("diaryId") @Valid @NotNull(message = "diaryId를 입력하세요.") Long diaryId) {
        Resource image = diaryService.getImageByDiaryId(diaryId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(image);
    }
}
