package com.example.demo;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.ReviewUpdateRequest; // 리뷰 수정을 위한 DTO 추가
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

// REST Controller for handling review-related API requests
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    /**
     * 현재 인증된 사용자의 userNumber를 반환합니다.
     * 인증되지 않은 경우 -1을 반환하거나 예외를 발생시킬 수 있습니다.
     */
    private int getCurrentUserNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // 인증되지 않은 사용자 또는 익명 사용자의 경우
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // 실제 UserDetails 객체에서 userNumber를 가져오는 로직 (예: 사용자 ID로 DB에서 조회)
        // 여기서는 예시로 principal이 사용자 ID(String)라고 가정하고 UserService를 통해 userNumber를 조회합니다.
        String userId = (String) authentication.getPrincipal();
        return userService.getUserById(userId)
                .map(u -> u.getUserNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }


    /**
     * 새 리뷰를 생성합니다.
     * @param reviewCreateRequest 생성할 리뷰의 데이터 (장소명, 평점, 본문 등)
     * @return 생성된 리뷰 정보 또는 오류 메시지
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewCreateRequest reviewCreateRequest) {
        // 현재 인증된 사용자의 userNumber를 사용합니다.
        int authenticatedUserNumber = getCurrentUserNumber();
        reviewCreateRequest.setUserNumber(authenticatedUserNumber); // DTO에 userNumber 설정

        // 필수 입력값 유효성 검사
        if (reviewCreateRequest.getPlaceName() == null || reviewCreateRequest.getPlaceName().isEmpty() ||
            reviewCreateRequest.getRating() == null) {
            return ResponseEntity.badRequest().body(new ReviewResponse(null, authenticatedUserNumber, null, null, null, null, null, null, null)); // 예시 응답, 실제 DTO에 맞게 조정 필요
        }

        // ReviewService를 호출하여 리뷰 생성
        ReviewResponse response = reviewService.createReview(reviewCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created 상태와 응답 본문 반환
    }

    /**
     * 특정 ID를 가진 리뷰를 조회합니다.
     * @param reviewId 조회할 리뷰의 ID
     * @return 조회된 리뷰 정보 또는 404 Not Found
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        Optional<ReviewResponse> review = reviewService.getReviewById(reviewId);
        return review.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 특정 사용자가 작성한 리뷰 목록을 조회합니다.
     * @param userNumber 리뷰를 작성한 사용자의 userNumber
     * @return 해당 사용자의 리뷰 목록
     */
    @GetMapping("/user/{userNumber}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable int userNumber) {
        // Corrected method name to match ReviewService
        List<ReviewResponse> reviews = reviewService.getReviewsByUserNumber(userNumber);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 기존 리뷰를 업데이트합니다.
     * @param reviewId 업데이트할 리뷰의 ID
     * @param reviewUpdateRequest 업데이트할 리뷰 데이터 (장소명, 평점, 본문 등)
     * @return 업데이트된 리뷰 정보 또는 오류 메시지
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable int reviewId,
                                                       @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        // 현재 인증된 사용자가 해당 리뷰의 작성자인지 확인하는 로직 추가 필요
        // 예시: reviewService.getReviewById(reviewId).orElseThrow(...).getUserNumber() == getCurrentUserNumber()
        try {
            ReviewResponse updatedReview = reviewService.updateReview(reviewId, reviewUpdateRequest);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 특정 ID를 가진 리뷰를 삭제합니다.
     * @param reviewId 삭제할 리뷰의 ID
     * @return 성공 시 204 No Content
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) {
        // 현재 인증된 사용자가 해당 리뷰의 작성자인지 확인하는 로직 추가 필요
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
