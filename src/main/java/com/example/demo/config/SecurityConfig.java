package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService; // UserService는 AuthenticationManager가 사용하므로 직접 주입 불필요

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder; // 이 임포트가 필요할 수 있습니다.
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // JwtTokenProvider만 생성자 주입
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
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
        return new CorsFilter(source);
    }

    // AuthenticationManager를 AuthenticationConfiguration에서 가져오도록 수정
    // UserService를 직접 주입받지 않습니다. Spring Security가 UserDetailsService를 찾습니다.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
           // .cors(cors -> cors.configurationSource(corsFilterSource())) // CorsFilter 제거해도
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // 로그인 및 회원가입 엔드포인트는 인증 없이 접근 허용
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                // 리뷰 관련 API는 인증 필요
                .requestMatchers("/api/reviews/**").authenticated()
                // 사용자 정보 조회 등 permitAll 필요한 다른 API
                .requestMatchers("/api/users/id/**").permitAll()
                .requestMatchers("/api/users/number/**").permitAll()
                .requestMatchers("/api/users/*/followers").permitAll()
                .requestMatchers("/api/users/*/followings").permitAll()

                // Likes API 경로 (좋아요는 인증된 사용자만 접근 가능)
                .requestMatchers("/api/likes/**").authenticated()

                // 웹 페이지 경로 (로그인/회원가입 페이지는 permitAll)
                .requestMatchers("/web/login", "/web/register").permitAll()
                .requestMatchers("/web/**").authenticated()

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
