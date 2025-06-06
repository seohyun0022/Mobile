package com.example.demo.dto;

public class LikeToggleResponse {
    private Long reviewId;
    private Integer userNumber;
    private boolean liked; // true면 좋아요 추가됨, false면 좋아요 취소됨
    private String message;

    public LikeToggleResponse(Long reviewId, Integer userNumber, boolean liked, String message) {
        this.reviewId = reviewId;
        this.userNumber = userNumber;
        this.liked = liked;
        this.message = message;
    }

    // Getters
    public Long getReviewId() { return reviewId; }
    public Integer getUserNumber() { return userNumber; }
    public boolean isLiked() { return liked; }
    public String getMessage() { return message; }
}
