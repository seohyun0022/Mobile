package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import com.example.demo.entity.Review; // Review 엔티티 임포트
import com.example.demo.entity.User; // User 엔티티 임포트 (Review 엔티티 변환 시 필요)
import com.example.demo.repository.UserRepository; // UserRepository 임포트 (toEntity 내부에서 User 조회 시 필요)

// 주의: toEntity() 메서드 내에서 UserRepository를 직접 주입받는 것은 DTO의 역할에 맞지 않습니다.
// DTO는 순수한 데이터 객체여야 하며, 서비스 로직을 포함해서는 안 됩니다.
// 따라서, 아래 toEntity() 구현은 User 엔티티를 직접 생성하는 임시 방편이며,
// 실제 애플리케이션에서는 서비스 계층에서 ReviewResponse를 Review 엔티티로 변환할 때
// User 엔티티를 조회하여 연결하는 것이 올바른 방식입니다.
// 여기서는 initData의 편의를 위해 DTO에 포함합니다.

/**
 * ReviewResponse는 클라이언트에게 리뷰 정보를 반환하기 위한 DTO (Data Transfer Object)입니다.
 * 이 클래스는 Review 엔티티의 주요 필드를 포함하며, 필요한 정보를 캡슐화하여 전송합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private int reviewId;
    private int userNumber;
    private String storeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imagePath;
    private String title;
    private String body;

    /**
     * ReviewResponse DTO를 Review 엔티티로 변환합니다.
     * 이 메서드는 DTO의 역할에 완전히 부합하지는 않지만,
     * 초기 데이터 설정(initData)의 편의를 위해 추가되었습니다.
     * 실제 애플리케이션에서는 서비스 계층에서 User 엔티티를 조회하여
     * Review 엔티티를 구성하는 것이 더 바람직합니다.
     *
     * @return 변환된 Review 엔티티
     */
    public Review toEntity() {
        Review review = new Review();
        review.setReviewId(this.reviewId);
        // userNumber만 가지고 User 엔티티를 완전하게 구성하기는 어렵습니다.
        // 여기서는 임시 User 객체를 생성하여 userNumber만 설정합니다.
        // 실제 JPA 저장 시에는 userNumber를 기반으로 User 엔티티를 조회하여 설정해야 합니다.
        // SpringMobileApplication의 initData에서 User 객체를 이미 가지고 있으므로,
        // DTO에서 toEntity()를 직접 호출하는 대신, initData에서 Review 엔티티를 직접 구성하는 방법도 고려할 수 있습니다.
        User user = new User();
        user.setUserNumber(this.userNumber);
        review.setUser(user); // 임시 User 객체 설정
        
        review.setStoreName(this.storeName);
        review.setLatitude(this.latitude);
        review.setLongitude(this.longitude);
        review.setImagePath(this.imagePath);
        review.setTitle(this.title);
        review.setBody(this.body);
        return review;
    }
}
