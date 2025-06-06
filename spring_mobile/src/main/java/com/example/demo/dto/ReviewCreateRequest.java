package com.example.demo.dto;

import java.math.BigDecimal;

public class ReviewCreateRequest {
    private Integer userNumber;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private BigDecimal rating; // Double 타입
    private String title;
    private String reviewText; // reviewText로 통일

    // All-args constructor: 인자 순서와 타입을 정확히 맞춰야 합니다.
    public ReviewCreateRequest(Integer userNumber, String placeName, BigDecimal latitude,
                               BigDecimal longitude, String imageUrl, BigDecimal rating, // <-- rating이 이곳에 위치
                               String title, String reviewText) {
        this.userNumber = userNumber;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.title = title;
        this.reviewText = reviewText;
    }

    // Default constructor (for JSON deserialization)
    public ReviewCreateRequest() {
    }

    // Getters
    public Integer getUserNumber() { return userNumber; }
    public String getPlaceName() { return placeName; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getRating() { return rating; }
    public String getTitle() { return title; }
    public String getReviewText() { return reviewText; }

    // Setters
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public void setTitle(String title) { this.title = title; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}
