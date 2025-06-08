package com.example.demo.dto;

import com.example.demo.entity.Review; // Review entity import
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // BigDecimal import 추가

/**
 * 리뷰 정보를 클라이언트에 반환할 때 사용하는 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor // Lombok annotation for no-argument constructor
@AllArgsConstructor // Lombok annotation for all-argument constructor (if used for all fields directly)
public class ReviewResponse {
    private Integer reviewId;
    private Integer userNumber;
    private String storeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imagePath;
    private String title;       // <-- Review 엔티티의 title과 일치하도록 추가
    private String body;        // <-- Review 엔티티의 body와 일치하도록 추가
    private BigDecimal rating;

    // Review 엔티티로부터 ReviewResponse DTO를 생성하는 생성자
    public ReviewResponse(Review review) {
        this.reviewId = review.getReviewId();
        this.userNumber = review.getUser().getUserNumber();
        this.storeName = review.getStoreName();
        this.latitude = review.getLatitude();
        this.longitude = review.getLongitude();
        this.imagePath = review.getImagePath();
        this.title = review.getTitle(); // <-- Review 엔티티의 title을 가져옴
        this.body = review.getBody();   // <-- Review 엔티티의 body를 가져옴
        this.rating = review.getRating();
    }

    // toEntity() 메서드는 ReviewResponse를 다시 Review 엔티티로 변환할 때 사용합니다.
    // 필요하다면 구현하세요. (예: 리뷰 업데이트 DTO가 아닌 조회 응답 DTO이므로 필수는 아닐 수 있습니다.)
    // public Review toEntity() {
    //     Review review = new Review();
    //     review.setReviewId(this.reviewId);
    //     // user 정보는 직접 설정해야 합니다.
    //     review.setStoreName(this.storeName);
    //     review.setLatitude(this.latitude);
    //     review.setLongitude(this.longitude);
    //     review.setImagePath(this.imagePath);
    //     review.setTitle(this.title);
    //     review.setBody(this.body);
    //     review.setRating(this.rating);
    //     return review;
    // }
}
