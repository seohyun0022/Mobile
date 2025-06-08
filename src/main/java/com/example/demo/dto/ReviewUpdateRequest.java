package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // BigDecimal import 추가

/**
 * 리뷰 업데이트 요청을 위한 DTO입니다.
 * 클라이언트로부터 리뷰의 특정 필드만 업데이트할 때 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor // Lombok annotation for no-argument constructor
@AllArgsConstructor // Lombok annotation for all-argument constructor (if needed for testing or specific use cases)
public class ReviewUpdateRequest {
    // userNumber는 업데이트 요청 시 현재 로그인한 사용자의 ID와 일치하는지 확인하는 데 사용됩니다.
    private Integer userNumber;

    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private String title;       // <-- 리뷰 제목 필드 추가 (Review 엔티티의 title과 매핑)
    private String reviewBody;  // <-- 리뷰 본문 필드 추가 (Review 엔티티의 body와 매핑)
    private BigDecimal rating;

    // 필요한 경우, 특정 필드만 업데이트할 수 있도록 부분적인 생성자를 추가할 수도 있습니다.
    // 하지만 @Setter가 있으므로 개별 필드 설정이 가능합니다.
}
