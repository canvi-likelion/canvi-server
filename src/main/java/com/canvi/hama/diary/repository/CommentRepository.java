package com.canvi.hama.diary.repository;

import com.canvi.hama.diary.entity.Comment;
import com.canvi.hama.diary.entity.Diary;
import com.canvi.hama.diary.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByDiaryId(Diary diaryId);

}
