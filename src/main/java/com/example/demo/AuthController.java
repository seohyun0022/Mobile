package com.example.demo;



import com.example.demo.dto.LoginRequest;

import com.example.demo.dto.LoginResponse;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AuthenticationManager; // AuthenticationManager 임포트


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; // AuthenticationManager를 주입받음

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager) { // 생성자 주입
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 사용자 로그인 처리 엔드포인트.
     * @param loginRequest 로그인 요청 DTO (ID, 비밀번호)
     * @return 로그인 응답 DTO (토큰, 사용자 정보)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // UserService의 login 메서드에 AuthenticationManager를 전달
            LoginResponse response = userService.login(loginRequest, authenticationManager); // <-- 수정된 부분
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 사용자 없음, 비밀번호 불일치 등의 로그인 실패 시 401 Unauthorized 반환
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
