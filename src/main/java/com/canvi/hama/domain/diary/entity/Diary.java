package com.canvi.hama.domain.diary.entity;

import com.canvi.hama.common.entity.BaseEntity;
import com.canvi.hama.domain.user.entity.User;
import jakarta.persistence.*;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Comment comment;

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

    public void setImage(Image image) { this.image = image; }

    public void setComment(Comment comment) { this.comment = comment; }
}
