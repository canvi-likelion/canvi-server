package com.canvi.hama.diary.repository;

import com.canvi.hama.diary.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // 특정 다이어리에 연결된 이미지를 찾기 위한 메서드
    Optional<Image> findByDiaryId(Long diaryId);
}
