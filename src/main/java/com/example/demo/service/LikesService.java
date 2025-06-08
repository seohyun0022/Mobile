package com.example.demo.service;

import com.example.demo.dto.LikeResponse;
import com.example.demo.entity.Like;
import com.example.demo.entity.LikeId;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.LikesRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public LikesService(LikesRepository likesRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public LikeResponse toggleLike(Long postId, int userNumber) {
        User user = userRepository.findByUserNumber(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userNumber: " + userNumber));

        Review review = reviewRepository.findById(postId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + postId));

        LikeId likeId = new LikeId(user.getUserNumber(), review.getReviewId());

        boolean isLikedNow;

        Optional<Like> existingLike = likesRepository.findById(likeId);

        if (existingLike.isPresent()) {
            likesRepository.delete(existingLike.get());
            isLikedNow = false;
        } else {
            Like newLike = new Like(user, review);
            likesRepository.save(newLike);
            isLikedNow = true;
        }

        long currentLikeCount = likesRepository.countByReview_ReviewId(review.getReviewId());

        return new LikeResponse(isLikedNow, currentLikeCount, postId, userNumber);
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(Long postId, Integer userNumber) { // userNumber를 Optional<Integer> 또는 Integer (nullable)로 변경
        Review review = reviewRepository.findById(postId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with ID: " + postId));

        long likeCount = likesRepository.countByReview_ReviewId(review.getReviewId());
        boolean isLikedByUser = false;

        // userNumber가 제공된 경우에만 사용자가 좋아요를 눌렀는지 확인
        if (userNumber != null) {
            User user = userRepository.findByUserNumber(userNumber).orElse(null);
            if (user != null) {
                LikeId likeId = new LikeId(user.getUserNumber(), review.getReviewId());
                isLikedByUser = likesRepository.existsById(likeId);
            }
        }
        
        return new LikeResponse(isLikedByUser, likeCount, postId, userNumber);
    }
}
