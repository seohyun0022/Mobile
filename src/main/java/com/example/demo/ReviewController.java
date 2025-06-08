package com.example.demo;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.ReviewUpdateRequest;
import com.example.demo.entity.Review;
import com.example.demo.entity.User; // User 엔티티 임포트
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository; // UserRepository 임포트
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // UserDetails 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository; // UserRepository 주입 추가

    @Autowired
    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
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


    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewCreateRequest reviewCreateRequest) {
        int authenticatedUserNumber = getCurrentUserNumber(); // 인증된 사용자 userNumber 가져오기
        // reviewCreateRequest.setUserNumber(authenticatedUserNumber); // DTO에 userNumber 설정 (클라이언트에서 보낼 필요 없음)
        ReviewResponse createdReview = reviewService.createReview(reviewCreateRequest, authenticatedUserNumber); // service 메서드 시그니처에 userNumber 추가
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Integer reviewId,
                                                       @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        int authenticatedUserNumber = getCurrentUserNumber();
        ReviewResponse updatedReview = reviewService.updateReview(reviewId, reviewUpdateRequest, authenticatedUserNumber);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        int authenticatedUserNumber = getCurrentUserNumber();
        reviewService.deleteReview(reviewId, authenticatedUserNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Integer reviewId) {
        Optional<ReviewResponse> review = reviewService.getReviewById(reviewId);
        return review.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userNumber}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Integer userNumber) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(userNumber);
        return ResponseEntity.ok(reviews);
    }
}
