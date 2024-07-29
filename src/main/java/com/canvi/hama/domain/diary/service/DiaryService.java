package com.canvi.hama.domain.diary.service;

import com.canvi.hama.domain.diary.dto.request.DiaryRequest;
import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.entity.Image;
import com.canvi.hama.domain.diary.enums.DiaryResponseStatus;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.repository.CommentRepository;
import com.canvi.hama.domain.diary.repository.DiaryRepository;
import com.canvi.hama.domain.diary.repository.ImageRepository;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private static final String IMAGE_DIRECTORY = "./user_images";
    private static final String IMAGE_FILE_FORMAT = "image_%d.jpg";

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void saveDiary(String username, DiaryRequest diaryRequest) {
        User user = getUserByUsername(username);

        Diary diary = Diary.builder()
                .user(user)
                .title(diaryRequest.title())
                .content(diaryRequest.content())
                .diaryDate(diaryRequest.diaryDate())
                .build();

        diaryRepository.save(diary);
    }

    @Transactional(readOnly = true)
    public List<Diary> getDiariesByUsername(String username) {
        User user = getUserByUsername(username);
        return diaryRepository.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Comment getCommentByDiaryId(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
        return commentRepository.findByDiary(diary)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
    }

    public void saveComment(Long diaryId, String comment) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        Comment saveComment = Comment.builder().diary(diary).comment(comment).build();

        commentRepository.save(saveComment);
    }

    @Transactional
    public void saveImageFromUrl(Long diaryId, String imageUrl) {
        Diary diary = getDiaryById(diaryId);
        Path imagePath = downloadImage(diaryId, imageUrl);
        saveImageMetadata(diary, imagePath);
    }

    @Transactional(readOnly = true)
    public Resource getImageByDiaryId(Long diaryId) {
        Image image = imageRepository.findByDiaryId(diaryId)
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

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
    }

    private Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
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

    private void saveImageMetadata(Diary diary, Path imagePath) {
        Image image = Image.builder()
                .diary(diary)
                .url(imagePath.toAbsolutePath().toString())
                .build();

        imageRepository.save(image);
    }
}
