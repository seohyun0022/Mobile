package com.example.demo.dto;

import java.math.BigDecimal;

public class ReviewCreateRequest {
    private Integer userNumber;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private String title;       // <-- Add this field for the review title/summary
    private String reviewBody;  // <-- Add this field for the main review body
    private BigDecimal rating;

    // Constructor matching the 8 arguments in SpringMobileApplication.java
    public ReviewCreateRequest(Integer userNumber, String placeName, BigDecimal latitude, BigDecimal longitude,
                               String imageUrl, String title, String reviewBody, BigDecimal rating) {
        this.userNumber = userNumber;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.title = title;
        this.reviewBody = reviewBody;
        this.rating = rating;
    }

    // Getters for all fields
    public Integer getUserNumber() { return userNumber; }
    public String getPlaceName() { return placeName; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }       // <-- Getter for title
    public String getReviewBody() { return reviewBody; } // <-- Getter for reviewBody
    public BigDecimal getRating() { return rating; }

    // Setters (if needed, Lombok @Setter can generate these)
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTitle(String title) { this.title = title; }
    public void setReviewBody(String reviewBody) { this.reviewBody = reviewBody; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
}
