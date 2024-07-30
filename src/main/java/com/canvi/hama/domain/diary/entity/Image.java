package com.canvi.hama.domain.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "url")
    @NotNull
    private String url;

    @Builder(access = AccessLevel.PRIVATE)
    public Image(Diary diary, String url) {
        this.diary = diary;
        this.url = url;
    }

    public static Image create(Diary diary, String url) {
        return Image.builder()
                .diary(diary)
                .url(url)
                .build();
    }
}
