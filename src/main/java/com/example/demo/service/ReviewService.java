package com.example.demo.service;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.ReviewUpdateRequest;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository; // UserRepository 주입

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, int userNumber) { // userNumber 파라미터 추가
        User user = userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with userNumber: " + userNumber));

        Review review = new Review();
        review.setUser(user); // User 엔티티 설정
        review.setStoreName(request.getPlaceName()); // 필드명 변경
        review.setImagePath(request.getImageUrl()); // 필드명 변경
        review.setLatitude(request.getLatitude());
        review.setLongitude(request.getLongitude());
        review.setTitle(request.getTitle());
        review.setBody(request.getReviewBody()); // 필드명 변경
        review.setRating(request.getRating());

        Review savedReview = reviewRepository.save(review);
        return new ReviewResponse(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Integer reviewId, ReviewUpdateRequest request, int userNumber) { // userNumber 파라미터 추가
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId));

        // 요청을 보낸 사용자와 리뷰 작성자가 일치하는지 확인
        if (review.getUser().getUserNumber() != userNumber) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this review.");
        }

        if (request.getPlaceName() != null) review.setStoreName(request.getPlaceName());
        if (request.getImageUrl() != null) review.setImagePath(request.getImageUrl());
        if (request.getLatitude() != null) review.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) review.setLongitude(request.getLongitude());
        if (request.getTitle() != null) review.setTitle(request.getTitle());
        if (request.getReviewBody() != null) review.setBody(request.getReviewBody());
        if (request.getRating() != null) review.setRating(request.getRating());

        Review updatedReview = reviewRepository.save(review);
        return new ReviewResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Integer reviewId, int userNumber) { // userNumber 파라미터 추가
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId));

        // 요청을 보낸 사용자와 리뷰 작성자가 일치하는지 확인
        if (review.getUser().getUserNumber() != userNumber) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Optional<ReviewResponse> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .map(ReviewResponse::new);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(Integer userNumber) {
        User user = userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with userNumber: " + userNumber));
        return reviewRepository.findByUser(user).stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }
}
