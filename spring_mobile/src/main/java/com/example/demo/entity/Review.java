package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*; // Spring Boot 3.x에서는 jakarta.persistence 사용
import java.math.BigDecimal; // DECIMAL 타입 매핑을 위해 추가

/**
 * Review entity maps to the 'Review' table in MySQL.
 * It directly includes store information (name, latitude, longitude) as fields.
 * 리뷰 엔티티는 MySQL의 'Review' 테이블에 매핑됩니다.
 * 가게 정보(이름, 위도, 경도)를 필드로 직접 포함합니다.
 */
@Entity
@Table(name = "Review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id // 기본 키를 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 전략을 사용합니다.
    @Column(name = "review_id") // 데이터베이스 컬럼명 지정
    private int reviewId;

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와의 다대일(N:1) 관계를 나타냅니다. (지연 로딩)
    @JoinColumn(name = "user_number", nullable = false) // Review 테이블의 'user_number' 컬럼이 User 엔티티의 기본 키를 참조합니다.
    private User user; // 리뷰를 작성한 User 엔티티

    @Column(name = "store_name", nullable = false, length = 100) // 가게 이름 컬럼
    private String storeName;

    @Column(name = "latitude", precision = 10, scale = 7) // 위도 컬럼 (DECIMAL(10, 7) 매핑)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7) // 경도 컬럼 (DECIMAL(10, 7) 매핑)
    private BigDecimal longitude;

    @Column(name = "image_path", length = 255) // 이미지 경로 컬럼
    private String imagePath;

    @Column(name = "title", length = 200) // 타이틀 컬럼
    private String title;

    @Column(name = "body", columnDefinition = "TEXT") // 본문 컬럼 (긴 텍스트를 위해 TEXT 타입 매핑)
    private String body;
}
