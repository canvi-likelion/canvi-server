package com.canvi.hama.domain.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "comment")
    @NotNull
    private Diary diary;

    @Column(name = "comment", columnDefinition = "TEXT")
    @NotNull
    private String comment;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(Diary diary, String comment) {
        this.diary = diary;
        this.comment = comment;
    }

    public static Comment create(Diary diary, String comment) {
        return Comment.builder()
                .diary(diary)
                .comment(comment)
                .build();
    }
}