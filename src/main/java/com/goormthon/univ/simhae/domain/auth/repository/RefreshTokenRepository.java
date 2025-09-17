package com.goormthon.univ.simhae.domain.auth.repository;

import com.goormthon.univ.simhae.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
    @Modifying
    @Query("update RefreshToken rt set rt.revoked=true where rt.user.id=:userId and rt.revoked=false")
    int revokeAllActiveByUser(@Param("userId") Long userId);
}
