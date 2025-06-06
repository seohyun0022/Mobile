package com.example.demo; 
import com.example.demo.dto.LikeRequest;
import com.example.demo.dto.LikeResponse;
import com.example.demo.service.LikesService;
import com.example.demo.service.UserService; // UserService 임포트
import com.example.demo.dto.UserResponse; // UserResponse DTO 임포트 (UserService.getUserById의 반환 타입)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikesService likesService;
    private final UserService userService; // UserService 주입

    @Autowired
    public LikeController(LikesService likesService, UserService userService) {
        this.likesService = likesService;
        this.userService = userService;
    }

    
    private int getCurrentUserNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. 인증 상태 확인
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        // 2. Principal(여기서는 사용자 로그인 ID) 가져오기
        String authenticatedUserId = authentication.getName(); // Spring Security의 User 객체는 getUsername() 대신 getName() 사용

        // 3. UserService를 통해 사용자 ID로 UserResponse DTO를 조회하여 userNumber 가져오기
        Optional<UserResponse> userResponseOptional = userService.getUserById(authenticatedUserId); // <-- Optional<UserResponse>로 변경
        if (userResponseOptional.isPresent()) {
            return userResponseOptional.get().getUserNumber(); // <-- UserResponse에서 userNumber 추출
        } else {
            // 사용자 ID는 인증되었으나 DB에서 userNumber를 찾을 수 없는 경우
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "인증된 사용자의 번호를 찾을 수 없습니다.");
        }
    }


    
    @PostMapping("/toggle")
    public ResponseEntity<LikeResponse> toggleLike(@RequestBody LikeRequest likeRequest) {
        // userNumber는 JWT 토큰을 통해 인증된 사용자 정보에서 가져옵니다.
        int userNumber = getCurrentUserNumber(); // 인증된 사용자 번호 가져오기

        if (likeRequest.getPostId() == null) {
            return ResponseEntity.badRequest().body(new LikeResponse("게시물 ID가 필요합니다.", false, 0L));
        }

        // Call the service to toggle the like
        LikeResponse response = likesService.toggleLike(likeRequest.getPostId(), userNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: GET /api/likes/status?postId=123
     */
    @GetMapping("/status")
    public ResponseEntity<LikeResponse> getLikeStatus(
            @RequestParam("postId") Long postId) {
        int userNumber = getCurrentUserNumber(); // 인증된 사용자 번호 가져오기

        if (postId == null) {
            return ResponseEntity.badRequest().body(new LikeResponse("게시물 ID가 필요합니다.", false, 0L));
        }

        LikeResponse response = likesService.getLikeStatusAndCount(postId, userNumber);
        return ResponseEntity.ok(response);
    }
}
