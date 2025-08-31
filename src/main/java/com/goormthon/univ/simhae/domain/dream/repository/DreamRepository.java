package com.goormthon.univ.simhae.domain.dream.repository;

import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DreamRepository extends JpaRepository<Dream, Long> {

    List<Dream> findTop7ByUserIdOrderByCreatedDateDesc(Long userId);

}
