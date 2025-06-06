package com.example.demo.service;

import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.ReviewUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ReviewService는 리뷰 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 리뷰 생성, 조회, 업데이트, 삭제 기능을 제공합니다.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new review.
     * @param request Review creation request DTO (ReviewCreateRequest)
     * @return Response DTO of the created review (ReviewResponse)
     * @throws IllegalArgumentException if a user corresponding to the userNumber is not found
     */
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        // Retrieve User entity using userNumber.
        // The error indicates findByUserNumber(String) is being called, but UserRepository expects int.
        // Assuming userNumber in DTO is int, and UserRepository.findByUserNumber expects int.
        User user = userRepository.findByUserNumber(request.getUserNumber())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + request.getUserNumber()));

        Review review = new Review();
        review.setUser(user);
        review.setStoreName(request.getPlaceName());

        // Assuming DTO's getLatitude(), getLongitude(), getRating() now return BigDecimal.
        // If they still return Double, you MUST change them in ReviewCreateRequest.java first.
        review.setLatitude(request.getLatitude());
        review.setLongitude(request.getLongitude());
        review.setRating(request.getRating());

        review.setImagePath(request.getImageUrl());
        review.setTitle(request.getPlaceName());
        review.setBody(request.getReviewText());

        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    /**
     * Retrieves a review with a specific ID.
     * @param reviewId ID of the review to retrieve
     * @return Optional object containing the response DTO of the retrieved review (ReviewResponse)
     */
    @Transactional(readOnly = true)
    public Optional<ReviewResponse> getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId.intValue())
                .map(this::convertToDto);
    }

    /**
     * Retrieves all reviews.
     * @return List of response DTOs for all reviews (List<ReviewResponse>)
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves reviews written by a specific user.
     * @param userNumber userNumber of the user who wrote the reviews
     * @return List of response DTOs for reviews written by the user (List<ReviewResponse>)
     * @throws IllegalArgumentException if a user corresponding to the userNumber is not found
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserNumber(int userNumber) {
        // The error indicates findByUserNumber(String) is being called, but UserRepository expects int.
        // Assuming UserRepository has findByUserNumber(int userNumber) and it's correctly used here.
        User user = userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + userNumber));

        return reviewRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing review.
     * @param reviewId ID of the review to update
     * @param request Review update request DTO containing the content to update (ReviewUpdateRequest)
     * @return Response DTO of the updated review (ReviewResponse)
     * @throws IllegalArgumentException if the review corresponding to reviewId is not found or if the user corresponding to userNumber is not found
     */
    @Transactional
    public ReviewResponse updateReview(int reviewId, ReviewUpdateRequest request) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // The error indicates findByUserNumber(String) is being called, but UserRepository expects int.
        // Assuming userNumber in DTO is int, and UserRepository.findByUserNumber expects int.
        User user = userRepository.findByUserNumber(request.getUserNumber())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + request.getUserNumber()));

        existingReview.setUser(user);
        existingReview.setStoreName(request.getPlaceName());

        // Assuming DTO's getLatitude(), getLongitude(), getRating() now return BigDecimal.
        // If they still return Double, you MUST change them in ReviewUpdateRequest.java first.
        existingReview.setLatitude(request.getLatitude());
        existingReview.setLongitude(request.getLongitude());
        existingReview.setRating(request.getRating());

        existingReview.setImagePath(request.getImageUrl());
        existingReview.setTitle(request.getPlaceName());
        existingReview.setBody(request.getReviewText());

        Review updatedReview = reviewRepository.save(existingReview);
        return convertToDto(updatedReview);
    }

    @Transactional
    public void deleteReview(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Converts a Review entity to a ReviewResponse DTO.
     * @param review The Review entity to convert.
     * @return The converted ReviewResponse DTO.
     */
    private ReviewResponse convertToDto(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getUser().getUserNumber(),
                review.getStoreName(),
                review.getLatitude(),
                review.getLongitude(),
                review.getImagePath(),
                review.getTitle(),
                review.getBody(),
                review.getRating()
        );
    }
}
