package com.canvi.hama.ai.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id", nullable = false)
    private int diaryId;

    @Column(name = "url", nullable = false)
    private String url;

}
