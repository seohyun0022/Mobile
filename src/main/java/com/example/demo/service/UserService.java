package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // Lazy 임포트는 더 이상 필요 없을 수 있지만, 혹시 몰라 유지합니다.
import org.springframework.security.authentication.AuthenticationManager; // AuthenticationManager 임포트
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // AuthenticationManager를 필드 주입에서 제거합니다.
    // @Lazy
    // @Autowired
    // private AuthenticationManager authenticationManager;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        com.example.demo.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));

        // ROLE_USER 권한을 부여하여 반환합니다.
        // 스프링 시큐리티의 User 객체는 사용자 이름, 비밀번호, 권한 목록을 인자로 받습니다.
        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // ROLE_USER 권한 추가
        );
    }

    @Transactional
    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.findById(request.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다: " + request.getId());
        }
        User newUser = new User();
        newUser.setId(request.getId());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setUserName(request.getUserName());
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            try {
                newUser.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("유효하지 않은 생년월일 형식입니다. OSCE-MM-DD 형식을 사용해주세요.", e);
            }
        }
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            newUser.setGender(request.getGender().charAt(0));
        }
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        User savedUser = userRepository.save(newUser);
        return new UserResponse(savedUser);
    }

    // login 메서드의 파라미터로 AuthenticationManager를 직접 주입받도록 수정
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest, AuthenticationManager authenticationManager) { // <-- 수정된 부분
        // authenticationManager 필드 대신 파라미터로 받은 authenticationManager 사용
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getId(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        com.example.demo.entity.User user = userRepository.findById(loginRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("인증 후 사용자를 찾을 수 없습니다."));
        return new LoginResponse(jwt, user.getId(), user.getUserName(), user.getUserNumber());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByNumber(int userNumber) {
        return userRepository.findByUserNumber(userNumber)
                .map(UserResponse::new);
    }

    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest updateRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("ID: " + id + " 에 해당하는 사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        if (updateRequest.getUserName() != null && !updateRequest.getUserName().isEmpty()) {
            user.setUserName(updateRequest.getUserName());
        }
        if (updateRequest.getDateOfBirth() != null && !updateRequest.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(LocalDate.parse(updateRequest.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("유효하지 않은 생년월일 형식입니다. OSCE-MM-DD 형식을 사용해주세요.", e);
            }
        }
        if (updateRequest.getGender() != null && !updateRequest.getGender().isEmpty()) {
            user.setGender(updateRequest.getGender().charAt(0));
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }
}
