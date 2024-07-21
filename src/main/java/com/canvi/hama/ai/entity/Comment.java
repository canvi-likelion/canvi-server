package com.canvi.hama.ai.entity;


import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id")
    private Integer diaryId;

    @Column(name = "id")
    private Integer userId;

    @Column(name = "text", nullable = false)
    private String text;
}
