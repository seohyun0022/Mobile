package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal; // BigDecimal import 추가

@Entity
@Table(name = "Review") // 테이블 이름은 올려주신 DDL과 동일하게 'Review'로 가정합니다.
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private User user;

    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Column(name = "image_path", length = 255) // DDL에 따라 nullable
    private String imagePath;

    // DDL에 DECIMAL(10, 7)로 정의되었으므로 BigDecimal 사용
    @Column(name = "latitude", precision = 10, scale = 7, nullable = true) // DDL에 따라 nullable
    private BigDecimal latitude;

    // DDL에 DECIMAL(10, 7)로 정의되었으므로 BigDecimal 사용
    @Column(name = "longitude", precision = 10, scale = 7, nullable = true) // DDL에 따라 nullable
    private BigDecimal longitude;

    @Column(name = "title", length = 200) // DDL에 따라 nullable
    private String title;

    @Column(name = "body", columnDefinition = "TEXT") // DDL에 따라 TEXT 타입 매핑
    private String body;

    // DDL에 DECIMAL(2, 1)로 정의되었으므로 BigDecimal 사용
    @Column(name = "rating", precision = 2, scale = 1, nullable = true) // DDL에 따라 nullable
    private BigDecimal rating;

    // 기본 생성자
    public Review() {}

    // 모든 필드를 포함하는 생성자 (옵션)
    public Review(Integer reviewId, User user, String storeName, String imagePath, BigDecimal latitude, BigDecimal longitude, String title, String body, BigDecimal rating) {
        this.reviewId = reviewId;
        this.user = user;
        this.storeName = storeName;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.body = body;
        this.rating = rating;
    }

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
}
