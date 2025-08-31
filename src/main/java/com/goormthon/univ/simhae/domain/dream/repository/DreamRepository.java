package com.goormthon.univ.simhae.domain.dream.repository;

import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DreamRepository extends JpaRepository<Dream, Long> {

    List<Dream> findTop7ByUserIdOrderByCreatedDateDesc(Long userId);

    // 월별 + 키워드(옵션)
    @Query("""
        SELECT d
        FROM Dream d
        WHERE d.dreamDate >= :startDate
          AND d.dreamDate <  :endDate
          AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(d.title)   LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(d.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY d.dreamDate DESC, d.id DESC
        """)
    List<Dream> findByMonthAndKeyword(
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate,
            @Param("keyword")   String keyword
    );

    // 일별
    List<Dream> findByDreamDate(LocalDate dreamDate);

}
