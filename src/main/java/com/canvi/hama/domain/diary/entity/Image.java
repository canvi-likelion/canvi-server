package com.canvi.hama.domain.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "url", nullable = false)
    private String url;
}
