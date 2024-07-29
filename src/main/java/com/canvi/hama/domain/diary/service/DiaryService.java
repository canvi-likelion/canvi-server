package com.canvi.hama.domain.diary.service;

import com.canvi.hama.domain.diary.dto.DiarySummaryResponse;
import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.entity.Image;
import com.canvi.hama.domain.diary.exception.DiaryException;
import com.canvi.hama.domain.diary.repository.CommentRepository;
import com.canvi.hama.domain.diary.repository.DiaryRepository;
import com.canvi.hama.domain.diary.repository.ImageRepository;
import com.canvi.hama.domain.diary.request.DiaryRequest;
import com.canvi.hama.domain.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import com.canvi.hama.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public void saveDiary(DiaryRequest diaryRequest) {
        User user = userService.getUserByUserId(diaryRequest.getUserId());
        boolean isExistDiary = diaryRepository.existsByUserIdAndDiaryDate(diaryRequest.getUserId(), diaryRequest.getDiaryDate());
        if (isExistDiary) {
            throw new DiaryException(DiaryResponseStatus.DIARY_ALREADY_EXISTS);
        }

        Diary diary = Diary.builder()
                .user(user)
                .title(diaryRequest.getTitle())
                .content(diaryRequest.getContent())
                .diaryDate(diaryRequest.getDiaryDate())
                .build();

        diaryRepository.save(diary);
    }

    public List<Diary> getDiariesByUserId(Long userId) {
        User user = userService.getUserByUserId(userId);
        return diaryRepository.findByUserId(userId);
    }


    public void saveComment(Long diaryId, Long userId, String comment) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));

        Comment saveComment = Comment.builder().diaryId(diary).userId(user).comment(comment).build();

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

    public DiarySummaryResponse getDiaryByDate(Long userId, LocalDate date) {
        User user = userService.getUserByUserId(userId);
        Diary diary = diaryRepository.findByUserIdAndDiaryDate(user.getId(), date)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.DIARY_NOT_FOUND));

        return new DiarySummaryResponse(diary);
    }
}
