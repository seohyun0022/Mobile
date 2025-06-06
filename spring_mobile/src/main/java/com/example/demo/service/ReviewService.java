package com.example.demo.service;

import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ReviewService는 리뷰 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 리뷰 생성, 조회, 업데이트, 삭제 기능을 제공합니다.
 */
@Service // 이 클래스가 서비스 계층의 컴포넌트임을 나타냅니다.
public class ReviewService {

    private final ReviewRepository reviewRepository; // Review 엔티티의 CRUD 작업을 위한 리포지토리
    private final UserRepository userRepository; // User 엔티티 조회에 사용될 리포지토리

    /**
     * ReviewService의 생성자입니다.
     * Spring의 의존성 주입(DI)을 통해 ReviewRepository와 UserRepository를 주입받습니다.
     * @param reviewRepository Review 엔티티 리포지토리
     * @param userRepository User 엔티티 리포지토리
     */
    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * 새로운 리뷰를 생성합니다.
     * @param request 리뷰 생성 요청 DTO (ReviewCreateRequest)
     * @return 생성된 리뷰의 응답 DTO (ReviewResponse)
     * @throws IllegalArgumentException userNumber에 해당하는 사용자가 없을 경우 발생
     */
    @Transactional // 메서드 실행 중 예외 발생 시 롤백을 보장합니다.
    public ReviewResponse createReview(ReviewCreateRequest request) {
        // userNumber를 사용하여 User 엔티티를 조회합니다.
        // 사용자가 존재하지 않으면 예외를 발생시킵니다.
        User user = userRepository.findByUserNumber(request.getUserNumber())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + request.getUserNumber()));

        // ReviewCreateRequest DTO의 데이터를 Review 엔티티로 변환합니다.
        Review review = new Review();
        review.setUser(user); // 조회된 User 엔티티를 설정합니다.
        review.setStoreName(request.getStoreName());
        review.setLatitude(request.getLatitude());
        review.setLongitude(request.getLongitude());
        review.setImagePath(request.getImagePath());
        review.setTitle(request.getTitle());
        review.setBody(request.getBody());

        // Review 엔티티를 데이터베이스에 저장합니다.
        Review savedReview = reviewRepository.save(review);

        // 저장된 Review 엔티티를 ReviewResponse DTO로 변환하여 반환합니다.
        return convertToDto(savedReview);
    }

    /**
     * 특정 ID를 가진 리뷰를 조회합니다.
     * @param reviewId 조회할 리뷰의 ID
     * @return 조회된 리뷰의 응답 DTO (ReviewResponse)를 포함하는 Optional 객체
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션을 설정하여 성능을 최적화합니다.
    public Optional<ReviewResponse> getReviewById(int reviewId) {
        // reviewId를 사용하여 Review 엔티티를 조회하고, Optional로 래핑하여 반환합니다.
        return reviewRepository.findById(reviewId)
                .map(this::convertToDto); // Review 엔티티가 존재하면 DTO로 변환합니다.
    }

    /**
     * 모든 리뷰를 조회합니다.
     * @return 모든 리뷰의 응답 DTO 리스트 (List<ReviewResponse>)
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        // 모든 Review 엔티티를 조회하고, 각 엔티티를 DTO로 변환하여 리스트로 반환합니다.
        return reviewRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 리뷰들을 조회합니다.
     * @param userNumber 리뷰를 작성한 사용자의 userNumber
     * @return 해당 사용자가 작성한 리뷰들의 응답 DTO 리스트 (List<ReviewResponse>)
     * @throws IllegalArgumentException userNumber에 해당하는 사용자가 없을 경우 발생
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserNumber(int userNumber) {
        // userNumber를 사용하여 User 엔티티를 조회합니다.
        User user = userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + userNumber));

        // 해당 User가 작성한 모든 리뷰를 조회합니다.
        return reviewRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 기존 리뷰를 업데이트합니다.
     * @param reviewId 업데이트할 리뷰의 ID
     * @param request 업데이트할 내용을 포함하는 리뷰 생성 요청 DTO (ReviewCreateRequest)
     * @return 업데이트된 리뷰의 응답 DTO (ReviewResponse)
     * @throws IllegalArgumentException reviewId에 해당하는 리뷰가 없거나 userNumber에 해당하는 사용자가 없을 경우 발생
     */
    @Transactional
    public ReviewResponse updateReview(int reviewId, ReviewCreateRequest request) {
        // reviewId를 사용하여 기존 Review 엔티티를 조회합니다.
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // userNumber를 사용하여 User 엔티티를 조회합니다.
        User user = userRepository.findByUserNumber(request.getUserNumber())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + request.getUserNumber()));

        // 기존 리뷰 엔티티의 필드를 업데이트합니다.
        existingReview.setUser(user); // User는 변경될 수 있으므로 업데이트
        existingReview.setStoreName(request.getStoreName());
        existingReview.setLatitude(request.getLatitude());
        existingReview.setLongitude(request.getLongitude());
        existingReview.setImagePath(request.getImagePath());
        existingReview.setTitle(request.getTitle());
        existingReview.setBody(request.getBody());

        // 업데이트된 Review 엔티티를 데이터베이스에 저장합니다.
        Review updatedReview = reviewRepository.save(existingReview);

        // 업데이트된 Review 엔티티를 ReviewResponse DTO로 변환하여 반환합니다.
        return convertToDto(updatedReview);
    }

    /**
     * 특정 ID를 가진 리뷰를 삭제합니다.
     * @param reviewId 삭제할 리뷰의 ID
     * @throws IllegalArgumentException reviewId에 해당하는 리뷰가 없을 경우 발생
     */
    @Transactional
    public void deleteReview(int reviewId) {
        // reviewId를 사용하여 리뷰의 존재 여부를 확인합니다.
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }
        // Review 엔티티를 데이터베이스에서 삭제합니다.
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Review 엔티티를 ReviewResponse DTO로 변환하는 헬퍼 메서드입니다.
     * @param review 변환할 Review 엔티티
     * @return 변환된 ReviewResponse DTO
     */
    private ReviewResponse convertToDto(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getUser().getUserNumber(), // User 엔티티에서 userNumber를 가져옵니다.
                review.getStoreName(),
                review.getLatitude(),
                review.getLongitude(),
                review.getImagePath(),
                review.getTitle(),
                review.getBody()
        );
    }
}
