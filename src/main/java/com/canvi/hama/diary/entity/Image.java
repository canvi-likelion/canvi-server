package com.canvi.hama.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import com.canvi.hama.diary.entity.Diary;
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
    @JoinColumn(name = "diary_id", referencedColumnName = "id", nullable = false)
    private Diary diary;

    @Column(name = "url", nullable = false)
    private String url;
}
