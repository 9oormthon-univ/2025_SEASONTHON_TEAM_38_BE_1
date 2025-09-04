package com.goormthon.univ.simhae.domain.user.service;


import com.goormthon.univ.simhae.domain.user.dto.UserRegisterResponse;
import com.goormthon.univ.simhae.domain.user.entity.User;
import com.goormthon.univ.simhae.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;


    /** 존재하면 그대로 반환, 없으면 생성 (idempotent) */
    @Transactional
    public UserRegisterResponse registerByHeader(String externalId) {
        return userRepository.findByExternalId(externalId)
                .map(u -> new UserRegisterResponse(u.getId(), u.getExternalId()))
                .orElseGet(() -> {
                    User saved = userRepository.save(
                            User.builder()
                                    .externalId(externalId)
                                    .build()
                    );
                    return new UserRegisterResponse(saved.getId(), saved.getExternalId());
                });
    }
}
