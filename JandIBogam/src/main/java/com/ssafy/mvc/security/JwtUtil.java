//package com.ssafy.mvc.security;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.Map;
//
//import com.ssafy.mvc.config.JwtConfig;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//
//@Component
//public class JwtUtil {
//    private final Key secretKey;
//    private final long accessTokenValidity;
//    private final long refreshTokenValidity;
//
//    // ① 생성자: JwtProperties 주입
//    @Autowired
//    public JwtUtil(JwtConfig jwtConfig) {
//        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//        this.accessTokenValidity = jwtConfig.getAccessTokenExpiration();
//        this.refreshTokenValidity = jwtConfig.getRefreshTokenExpiration();
//    }
//
//    // ② 액세스 토큰 생성 stub
//    public String generateAccessToken(String subject, Map<String,Object> claims) {
//        long now = System.currentTimeMillis();
//        return Jwts.builder()
//                .setClaims(claims)                             // 커스텀 클레임(userId, roles 등)
//                .setSubject(subject)                           // 토큰의 주체(대개 username 또는 userId)
//                .setIssuedAt(new Date(now))                    // 발급 시각
//                .setExpiration(new Date(now + accessTokenValidity)) // 만료 시각
//                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 알고리즘 + 비밀키
//                .compact();
//    }
//
//
//    // ③ 리프레시 토큰 생성 stub
//    public String generateRefreshToken(String subject) {
//        long now = System.currentTimeMillis();
//        return Jwts.builder()
//                .setSubject(subject)
//                .setIssuedAt(new Date(now))
//                .setExpiration(new Date(now + refreshTokenValidity)) // 길게 설정된 리프레시 만료
//                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//
//    // ④ 토큰 검증 stub
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);  // 서명 검증 & 만료 검증 동시 수행
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            // 서명 실패, 만료, 포맷 오류 등 모든 예외는 false 처리
//            return false;
//        }
//    }
//
//    // ⑤ 토큰에서 클레임 추출 stub
//    public Claims getClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();  // payload(클레임) 부분 반환
//    }
//
////    로그인한 유저의 토큰정보를 갖고와서 userId를 확인하기(모든 api에서 거의 쓰이니 따로 메서드로 빼기)
//    public int getUserIdFromRequest(HttpServletRequest request) {
//
//        String token = request.getHeader("Authorization").substring(7);
//        System.out.println("token : " + token);
//        Claims claims = getClaims(token);
//        System.out.println("claims : " + claims.toString());
//
//        return claims.get("id", Integer.class);
//    }
//
//}
