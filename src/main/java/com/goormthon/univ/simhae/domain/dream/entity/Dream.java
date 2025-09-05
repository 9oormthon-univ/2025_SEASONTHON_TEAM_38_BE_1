package com.goormthon.univ.simhae.domain.dream.entity;


import com.goormthon.univ.simhae.domain.dream.entity.value.Category;
import com.goormthon.univ.simhae.domain.user.entity.User;
import com.goormthon.univ.simhae.global.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "dream")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dream extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    private String title;

    private String emoji;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "dream_date")
    private LocalDate dreamDate;

    @Column(columnDefinition = "TEXT")
    private String interpretation;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

}
