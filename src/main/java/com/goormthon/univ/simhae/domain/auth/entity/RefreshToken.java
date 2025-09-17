package com.goormthon.univ.simhae.domain.auth.entity;

import com.goormthon.univ.simhae.domain.user.entity.User;
import com.goormthon.univ.simhae.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="refresh_token",
        indexes = @Index(name="ix_refresh_user", columnList="user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="token_hash", nullable=false, length=128)
    private String tokenHash;                // 원문 저장 금지(해시 보관)

    @Column(name="jti", nullable=false, length=64)
    private String jti;                      // 토큰 고유 ID(재사용 감지 보조)

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="revoked", nullable=false)
    private boolean revoked;

    @Column(name="replaced_by", length=128)
    private String replacedBy;               // 회전 후 새 토큰의 해시(옵션)
}
