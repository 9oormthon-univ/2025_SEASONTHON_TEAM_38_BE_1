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
        WHERE d.user.id = :userId
          AND d.dreamDate >= :startDate
          AND d.dreamDate <  :endDate
          AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(d.title)   LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY d.dreamDate DESC, d.id DESC
        """)
    List<Dream> findByUserAndMonthAndKeyword(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate,
            @Param("keyword")   String keyword
    );

    // 일별
    List<Dream> findByUser_IdAndDreamDate(Long userId, LocalDate dreamDate);

    Optional<Dream> findByIdAndUser_Id(Long id, Long userId);

    long deleteByIdAndUser_Id(Long id, Long userId);
}
