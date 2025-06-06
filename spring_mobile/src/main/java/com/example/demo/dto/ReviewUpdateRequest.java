package com.example.demo.dto;

import java.math.BigDecimal;

public class ReviewUpdateRequest {
    private String placeName;
    private String userNumber;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private BigDecimal rating;
    private String title;
    private String reviewText; // Changed to reviewText to match the request body

    // Getters and Setters
    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public String getUserNumber() {return userNumber;}
    public void setUserNumber(String userNumber) {this.userNumber = userNumber;}
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}
