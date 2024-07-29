package com.canvi.hama.domain.diary.repository;

import com.canvi.hama.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByUserId(Long userId);

    Optional<Diary> findByUserIdAndDiaryDate(Long userId, LocalDate date);
    boolean existsByUserIdAndDiaryDate(Long userId, LocalDate date);
}
