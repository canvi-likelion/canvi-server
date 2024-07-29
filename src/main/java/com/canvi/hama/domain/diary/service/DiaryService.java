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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void saveDiary(String username, DiaryRequest diaryRequest) {
        User user = getUserByUsername(username);

        Diary diary = Diary.builder()
                .user(user)
                .title(diaryRequest.getTitle())
                .content(diaryRequest.getContent())
                .diaryDate(diaryRequest.getDiaryDate())
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
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        Comment saveComment = Comment.builder().diary(diary).comment(comment).build();

        commentRepository.save(saveComment);
    }

    public void saveImageFromUrl(Long diaryId, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream in = url.openStream();

            File directory = new File("./user_images");
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory if it doesn't exist
            }
            File file = new File(directory, "image_" + diaryId + ".jpg");

            FileOutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();

            Diary diary = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

            Image image = Image.builder()
                    .diary(diary)
                    .url(file.getAbsolutePath())
                    .build();

            imageRepository.save(image);
        } catch (Exception e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public byte[] getImageByDiaryId(Long diaryId) {
        Image image = imageRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        try {
            File imgFile = new File(image.getUrl());
            return Files.readAllBytes(imgFile.toPath());
        } catch (IOException e) {
            throw new DiaryException(DiaryResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
    }
}
