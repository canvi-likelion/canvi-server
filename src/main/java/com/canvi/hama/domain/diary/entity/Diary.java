package com.canvi.hama.domain.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import com.canvi.hama.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Column(name = "title")
    @NotNull
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    @NotNull
    private String content;

    @Column(name = "diary_date")
    @NotNull
    private LocalDate diaryDate;

    @Builder(access = AccessLevel.PRIVATE)
    private Diary(User user, String title, String content, LocalDate diaryDate) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.diaryDate = diaryDate;
    }

    public static Diary create(User user, String title, String content, LocalDate diaryDate) {
        return Diary.builder()
                .user(user)
                .title(title)
                .content(content)
                .diaryDate(diaryDate)
                .build();
    }
}
