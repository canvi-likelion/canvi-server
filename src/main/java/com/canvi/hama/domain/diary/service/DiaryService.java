package com.canvi.hama.domain.diary.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.dto.response.CommentGetResponse;
import com.canvi.hama.domain.diary.dto.response.DiaryGetListResponse;
import com.canvi.hama.domain.diary.dto.response.DiaryGetResponse;
import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.entity.Image;
import com.canvi.hama.domain.diary.enums.DiaryResponseStatus;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.repository.CommentRepository;
import com.canvi.hama.domain.diary.repository.DiaryRepository;
import com.canvi.hama.domain.diary.repository.ImageRepository;
import com.canvi.hama.domain.user.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.canvi.hama.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private static final String IMAGE_DIRECTORY = "./user_images";
    private static final String IMAGE_FILE_FORMAT = "image_%d.jpg";

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    public void saveDiary(UserDetails userDetails, DiaryRequest diaryRequest) {
        User user = userService.getUserFromUserDetails(userDetails);
        Diary diary = Diary.create(user, diaryRequest.title(), diaryRequest.content(), diaryRequest.diaryDate());
        diaryRepository.save(diary);
    }

    @Transactional(readOnly = true)
    public DiaryGetListResponse getDiariesByUsername(UserDetails userDetails) {
        User user = userService.getUserFromUserDetails(userDetails);

        List<Diary> diaries =  diaryRepository.findAllByUser(user);
        List<DiaryGetResponse> diaryGetResponseList = DiaryGetResponse.fromDiaryList(diaries);

        return new DiaryGetListResponse(diaryGetResponseList);
    }

    public DiaryGetResponse getDiaryByDate(UserDetails userDetails, String dateStr) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new DiaryException(DiaryResponseStatus.DIARY_NOT_FOUND);
        }

        User user = userService.getUserFromUserDetails(userDetails);
        Diary diary = diaryRepository.findByUserIdAndDiaryDate(user.getId(), date)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.DIARY_NOT_FOUND));

        return new DiaryGetResponse(diary);
    }

    @Transactional(readOnly = true)
    public CommentGetResponse getCommentByDiaryId(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        CommentGetResponse commentGetResponse = new CommentGetResponse(diary.getComment().getId(), diary.getComment().getComment());
        return commentGetResponse;
    }

    @Transactional
    public void saveComment(Long diaryId, String comment) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        Comment saveComment = Comment.create(diary, comment);
        try {
            diary.setComment(saveComment);
            commentRepository.save(saveComment);
            diaryRepository.save(diary);
        } catch (BaseException e) {
            throw new DiaryException(DiaryResponseStatus.DATABASE_INSERT_ERROR);
        }
    }

    @Transactional
    public void saveImageFromUrl(Long diaryId, String imageUrl) {
        Diary diary = getDiaryById(diaryId);
        Path imagePath = downloadImage(diaryId, imageUrl);
        Image image = Image.create(diary, imagePath.toAbsolutePath().toString());
        try {
            diary.setImage(image);
            imageRepository.save(image);
            diaryRepository.save(diary);
        } catch (BaseException e) {
            throw new DiaryException(DiaryResponseStatus.DATABASE_INSERT_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Resource getImageByDiaryId(Long diaryId) {
        Diary diary = getDiaryById(diaryId);

        Image image = imageRepository.findByDiaryId(diary.getId())
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
        try {
            Path imagePath = Paths.get(image.getUrl());
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new DiaryException(DiaryResponseStatus.NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.DIARY_NOT_FOUND));
    }

    private Path downloadImage(Long diaryId, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Path filePath = getImageFilePath(diaryId);
            Files.createDirectories(filePath.getParent());

            try (InputStream in = url.openStream();
                 OutputStream out = Files.newOutputStream(filePath)) {
                in.transferTo(out);
            }

            return filePath;
        } catch (IOException e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Path getImageFilePath(Long diaryId) {
        return Paths.get(IMAGE_DIRECTORY, String.format(IMAGE_FILE_FORMAT, diaryId));
    }

}