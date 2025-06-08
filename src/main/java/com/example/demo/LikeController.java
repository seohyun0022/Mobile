package com.example.demo;

import com.example.demo.dto.LikeRequest;
import com.example.demo.dto.LikeResponse;
import com.example.demo.entity.User; // User 엔티티 임포트
import com.example.demo.repository.UserRepository; // UserRepository 임포트
import com.example.demo.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // UserDetails 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikesService likesService;
    private final UserRepository userRepository; // UserRepository 주입 추가

    @Autowired
    public LikeController(LikesService likesService, UserRepository userRepository) {
        this.likesService = likesService;
        this.userRepository = userRepository;
    }

    /**
     * 현재 인증된 사용자의 userNumber를 반환합니다.
     * 인증되지 않은 경우 UNAUTHORIZED 예외를 던집니다.
     * @return 현재 사용자의 userNumber
     * @throws ResponseStatusException 인증되지 않은 경우 401 UNAUTHORIZED
     */
    private int getCurrentUserNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // authentication.getPrincipal()은 보통 UserDetails 타입의 객체를 반환합니다.
        // UserDetails에서 사용자 ID(username)를 가져와서 UserRepository를 통해 userNumber를 조회합니다.
        Object principal = authentication.getPrincipal();
        String userId;

        if (principal instanceof UserDetails) {
            userId = ((UserDetails) principal).getUsername(); // UserDetails 객체에서 사용자 ID(username) 가져오기
        } else if (principal instanceof String) {
            userId = (String) principal; // 가끔 String으로 직접 들어올 수도 있으므로 대비
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid principal type");
        }

        // 사용자 ID를 통해 User 엔티티를 조회하여 userNumber를 가져옵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found in database: " + userId));

        return user.getUserNumber();
    }


    /**
     * 게시물에 대한 '좋아요'를 토글(추가/취소)합니다.
     * @param likeRequest 좋아요 요청 DTO (postId, userNumber)
     * @return '좋아요' 작업 결과 (isLiked, likeCount 등)
     */
    @PostMapping
    public ResponseEntity<LikeResponse> toggleLike(@RequestBody LikeRequest likeRequest) {
        int authenticatedUserNumber = getCurrentUserNumber(); // 인증된 사용자 userNumber 가져오기
        // likeRequest.setUserNumber(authenticatedUserNumber); // 클라이언트에서 보낼 필요 없음

        // postId는 Long 타입이므로, likeRequest에서 직접 가져와 사용합니다.
        LikeResponse response = likesService.toggleLike(likeRequest.getPostId(), authenticatedUserNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시물에 대한 좋아요 상태 및 총 좋아요 수를 조회합니다.
     * @param postId 조회할 게시물(리뷰)의 ID
     * @return 해당 게시물의 좋아요 상태 및 총 좋아요 수
     */
    @GetMapping("/{postId}/status")
    public ResponseEntity<LikeResponse> getLikeStatus(@PathVariable Long postId) {
        // 이 API는 인증 없이 조회 가능하도록 설정되었으므로, 현재 사용자 userNumber를 가져오지 않습니다.
        // 만약 사용자가 좋아요를 눌렀는지 여부(isLiked)를 특정 사용자에 대해 알고 싶다면,
        // 이 메서드에도 getCurrentUserNumber()를 추가하고 userNumber 파라미터를 넘겨야 합니다.
        // 현재는 전체 좋아요 개수만 반환하는 것으로 가정합니다.
        // likesService.getLikeStatus(postId, optionalUserNumber); // userNumber를 Optional로 넘길 수 있음
        LikeResponse response = likesService.getLikeStatus(postId, null); // userNumber를 넘기지 않음
        return ResponseEntity.ok(response);
    }
}
