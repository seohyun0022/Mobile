package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Spring Security의 User 클래스
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰을 생성하고 유효성을 검증하는 유틸리티 클래스입니다.
 */
@Component
public class JwtTokenProvider {

	 private final Key key;
	    private final long expiration;

	    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
	                            @Value("${jwt.expiration}") long expiration) {
	        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
	        this.key = Keys.hmacShaKeyFor(keyBytes);
	        this.expiration = expiration;
	    }

	    // JWT 토큰 생성
	    public String generateToken(Authentication authentication) {
	        String authorities = authentication.getAuthorities().stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.joining(","));

	        Date now = new Date();
	        Date expiryDate = new Date(now.getTime() + expiration);

	        return Jwts.builder()
	                .setSubject(authentication.getName())
	                .claim("roles", authorities) // roles 클레임은 이미 올바르게 추가되고 있습니다.
	                .setIssuedAt(now)
	                .setExpiration(expiryDate)
	                .signWith(key, SignatureAlgorithm.HS256)
	                .compact();
	    }

	    // JWT 토큰으로부터 인증 정보 조회
	    public Authentication getAuthentication(String token) {
	        Claims claims = Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();

	        // "roles" 클레임에서 권한 정보 가져오기
	        Object rolesObject = claims.get("roles"); // <-- 이 부분에서 null 또는 비어있는지 확인 필요
	        Collection<? extends GrantedAuthority> authorities;

	        if (rolesObject != null) {
	            String rolesString = rolesObject.toString();
	            // rolesString이 비어있지 않은지 확인
	            if (rolesString.trim().isEmpty()) {
	                // 빈 문자열인 경우, 기본 권한 부여 (예: ROLE_ANONYMOUS 또는 빈 리스트)
	                authorities = Collections.emptyList();
	            } else {
	                // 쉼표로 분리된 문자열을 GrantedAuthority 객체 리스트로 변환
	                authorities = Arrays.stream(rolesString.split(","))
	                        .map(SimpleGrantedAuthority::new) // <-- 여기서 IllegalArgumentException 발생
	                        .collect(Collectors.toList());
	            }
	        } else {
	            // "roles" 클레임 자체가 없는 경우, 빈 권한 리스트 반환
	            authorities = Collections.emptyList();
	        }

	        // UserDetails 객체 생성 (사용자 ID, 비밀번호는 null, 권한 목록)
	        // JWT 기반 인증에서는 비밀번호를 다시 조회하지 않으므로 null
	        User principal = new User(claims.getSubject(), "", authorities);

	        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true; // 토큰이 성공적으로 파싱되면 유효함
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다."); // 잘못된 서명 또는 형식 오류
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다."); // 토큰 만료
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다."); // 지원되지 않는 형식의 토큰
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다: 비어있거나 올바르지 않습니다."); // 토큰이 비어있거나 형식이 잘못됨
        }
        return false; // 유효성 검사 실패
    }
}
