package com.goormthon.univ.simhae.domain.user.entity;


import com.goormthon.univ.simhae.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_user_apple_sub", columnNames="apple_sub"))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="apple_sub", nullable=false, length=64)
    private String appleSub;

    @Column(name="email")
    private String email;
}
