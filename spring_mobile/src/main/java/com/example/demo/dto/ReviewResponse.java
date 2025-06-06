package com.example.demo.dto;

import java.math.BigDecimal;

public class ReviewResponse {
    private Integer reviewId;
    private Integer userNumber;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private String title;
    private String reviewText;
    private BigDecimal rating; //

    // 기존 생성자 (9개 인자)
    public ReviewResponse(Integer reviewId, Integer userNumber, String placeName,
                          BigDecimal latitude, BigDecimal longitude, String imageUrl,
                          String title, String reviewText, BigDecimal rating) {
        this.reviewId = reviewId;
        this.userNumber = userNumber;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.title = title;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    // <-- 유효성 검사용으로 사용될 수 있는 새로운 생성자 추가 (선택적)
    // 이 생성자는 에러 메시지처럼 일부 필드만 필요할 때 사용할 수 있습니다.
    // 하지만 가급적이면 완전한 DTO 객체를 반환하는 것이 좋습니다.
    // 현재 문제 해결을 위해서는 아래 createReview의 badRequest 부분만 9개 파라미터 생성자로 맞춰주면 됩니다.

    // Getters
    public Integer getReviewId() { return reviewId; }
    public Integer getUserNumber() { return userNumber; }
    public String getPlaceName() { return placeName; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getReviewText() { return reviewText; }
    public BigDecimal getRating() { return rating; }

    // Setters (필요하다면 추가)
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTitle(String title) { this.title = title; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
}
