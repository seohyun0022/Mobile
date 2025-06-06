package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO defining the data received from the client for a review creation request.
 * 클라이언트로부터 리뷰 생성 요청 시 받는 데이터를 정의하는 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    private int userNumber;      // 리뷰 작성자의 user_number
    private String storeName;    // 가게 이름
    private BigDecimal latitude; // 위도 (정확도를 위해 BigDecimal 사용)
    private BigDecimal longitude; // 경도 (정확도를 위해 BigDecimal 사용)
    private String imagePath;    // 이미지 경로 (선택 사항)
    private String title;        // 리뷰 제목
    private String body;         // 리뷰 본문 내용
}
