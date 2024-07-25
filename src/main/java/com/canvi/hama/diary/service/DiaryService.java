package com.canvi.hama.diary.service;

import com.canvi.hama.diary.entity.Comment;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.entity.Image;
import com.canvi.hama.diary.exception.DiaryException;
import com.canvi.hama.diary.repository.CommentRepository;
import com.canvi.hama.diary.repository.DiaryRepository;
import com.canvi.hama.diary.repository.ImageRepository;
import com.canvi.hama.diary.request.DiaryRequest;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import com.canvi.hama.domain.user.domain.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository, ImageRepository imageRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public void saveDiary(DiaryRequest diaryRequest) {
        User user = getUserByUserId(diaryRequest.getUserId());

        Diary diary = Diary.builder()
                .user(user)
                .title(diaryRequest.getTitle())
                .content(diaryRequest.getContent())
                .diaryDate(diaryRequest.getDiaryDate())
                .build();

        diaryRepository.save(diary);
    }

    public List<Diary> getDiariesByUserId(Long userId) {
        return diaryRepository.findByUserId(userId);
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DiaryException(DiaryResponseStatus.NOT_FOUND));
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
}
