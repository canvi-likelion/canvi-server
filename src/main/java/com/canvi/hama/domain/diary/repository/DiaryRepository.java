package com.canvi.hama.domain.diary.repository;

import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findAllByUser(User user);

    Optional<Diary> findByUserIdAndDiaryDate(Long id, LocalDate date);

}
