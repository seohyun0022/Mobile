package com.example.demo.security;

import jakarta.servlet.FilterChain; // 서블릿 필터 체인
import jakarta.servlet.ServletException; // 서블릿 예외
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 객체
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답 객체
import org.springframework.security.core.Authentication; // 스프링 시큐리티 인증 객체
import org.springframework.security.core.context.SecurityContextHolder; // 보안 컨텍스트 홀더
import org.springframework.util.StringUtils; // 문자열 유틸리티
import org.springframework.web.filter.OncePerRequestFilter; // 요청당 한 번만 실행되는 필터

import java.io.IOException; // 입출력 예외

/**
 * JWT 토큰을 사용하여 요청을 인증하는 필터입니다.
 * 모든 HTTP 요청에 대해 JWT 토큰의 유효성을 검사하고, 유효한 경우 SecurityContextHolder에 인증 정보를 설정합니다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider tokenProvider; // JWT 토큰 제공자 (주입받아 사용)

    // 생성자를 통해 JwtTokenProvider를 주입받습니다.
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, // 현재 HTTP 요청
                                    HttpServletResponse response, // 현재 HTTP 응답
                                    FilterChain filterChain) throws ServletException, IOException { // 다음 필터 체인
        try {
            String jwt = getJwtFromRequest(request); // HTTP 요청에서 JWT 토큰 추출

            // 추출된 JWT 토큰이 유효한지 검증하고, 유효하다면 인증 정보를 설정합니다.
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 토큰이 유효하면 JwtTokenProvider를 통해 Authentication 객체를 가져옵니다.
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                // 가져온 Authentication 객체를 SecurityContextHolder에 설정하여 현재 요청의 사용자를 인증된 상태로 만듭니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // JWT 토큰 처리 중 발생할 수 있는 예외를 로깅합니다.
            logger.error("보안 컨텍스트에 사용자 인증 정보를 설정할 수 없습니다.", ex);
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청을 전달합니다.
    }

    /**
     * HTTP 요청의 'Authorization' 헤더에서 'Bearer ' 접두사를 제거하여 JWT 토큰 문자열을 추출합니다.
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 문자열 (없으면 null)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authorization 헤더 값 가져오기
        // 헤더 값이 존재하고 'Bearer '로 시작하면 접두사를 제거하고 토큰 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "는 7글자이므로 그 이후부터 추출
        }
        return null; // 토큰이 없거나 형식이 맞지 않으면 null 반환
    }
}
