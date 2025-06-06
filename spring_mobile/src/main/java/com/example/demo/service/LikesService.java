package com.example.demo.service;



import com.example.demo.entity.Like;
import com.example.demo.dto.LikeResponse;
import com.example.demo.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Used for transactional operations

import java.util.Optional; // Used for handling nullable results

// Service layer for handling like-related business logic
@Service // Indicates that this is a service component
public class LikesService {

    private final LikeRepository likeRepository; // Inject LikeRepository

    @Autowired // Enables automatic dependency injection
    public LikesService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    /**
     * Toggles the like status for a given post and user.
     * If the user has already liked the post, it unlikes it.
     * If the user has not liked the post, it likes it.
     *
     * @param postId     The ID of the post.
     * @param userNumber The unique number of the user.
     * @return LikeResponse containing the new status, message, and updated like count.
     */
    @Transactional // Ensures the entire method executes as a single transaction (all or nothing)
    public LikeResponse toggleLike(Long postId, int userNumber) {
        // Check if the user has already liked this post
        Optional<Like> existingLike = likeRepository.findByUserNumberAndPostId(userNumber, postId);

        boolean likedStatus; // To store the final liked status
        String message;      // Message to send back to the client

        if (existingLike.isPresent()) {
            // If already liked, then unlike (delete the like)
            likeRepository.deleteByUserNumberAndPostId(userNumber, postId);
            likedStatus = false;
            message = "좋아요를 취소했습니다.";
            // Note: @Transactional ensures delete is committed to DB
        } else {
            // If not liked, then like (save a new like)
            Like newLike = new Like(postId, userNumber);
            likeRepository.save(newLike);
            likedStatus = true;
            message = "좋아요를 눌렀습니다.";
            // Note: @Transactional ensures save is committed to DB
        }

        // Get the updated total like count for the post
        Long updatedLikeCount = likeRepository.countByPostId(postId);

        return new LikeResponse(message, likedStatus, updatedLikeCount);
    }

    /**
     * Retrieves the current like status and total like count for a specific post and user.
     *
     * @param postId     The ID of the post.
     * @param userNumber The unique number of the user.
     * @return LikeResponse containing the current status, message, and current like count.
     */
    @Transactional(readOnly = true) // readOnly = true optimizes for read operations
    public LikeResponse getLikeStatusAndCount(Long postId, int userNumber) {
        // Check if the user has liked this post
        boolean liked = likeRepository.findByUserNumberAndPostId(userNumber, postId).isPresent();

        // Get the total like count for the post
        Long likeCount = likeRepository.countByPostId(postId);

        String message = liked ? "이 게시물을 좋아합니다." : "이 게시물을 좋아하지 않습니다.";

        return new LikeResponse(message, liked, likeCount);
    }
}