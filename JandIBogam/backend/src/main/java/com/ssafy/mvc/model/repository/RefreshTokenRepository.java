package com.ssafy.mvc.model.repository;

import com.ssafy.mvc.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //**refreshToken 데이터 접근 인터페이스**
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByLoginId(String loginId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByLoginId(String loginId);
}
