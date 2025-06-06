package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for Spring Security settings.
 * Includes password encryption and CORS configuration.
 * Updated for Spring Security 6.x (Spring Boot 3.x) and Likes API.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (람다 스타일)
            .cors(cors -> cors.configurationSource(corsFilterSource())) // CORS 설정 적용 (람다 스타일)
            .authorizeHttpRequests(authorize -> authorize // authorizeRequests() 대신 authorizeHttpRequests() 사용
                // API 경로 허용
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users/id/**").permitAll()
                .requestMatchers("/api/users/number/**").permitAll()
                .requestMatchers("/api/users/{userNumber}/followers").permitAll()
                .requestMatchers("/api/users/{userNumber}/followings").permitAll()
                .requestMatchers("/api/reviews/**").permitAll()
                // Likes API 경로 추가 (인증된 사용자만 접근 가능하도록 설정)
                .requestMatchers("/api/likes/**").authenticated() // Likes 관련 API는 인증 필요
                // 웹 페이지 경로 허용
                .requestMatchers("/web/login", "/web/register").permitAll()
                .requestMatchers("/web/**").authenticated() // /web 경로의 나머지 페이지는 인증 필요
                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );
            // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT 사용 시 세션 비활성화

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsFilterSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:8080",
                "http://10.0.2.2:8080"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
