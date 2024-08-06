package com.canvi.hama.domain.diary.repository;

import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByDiaryId(Long diaryId);

}
