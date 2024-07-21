package com.canvi.hama.diary.repository;

import com.canvi.hama.diary.entity.Diary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    List<Diary> findByUserId(Integer userId);

}
