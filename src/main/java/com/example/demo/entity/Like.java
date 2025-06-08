package com.example.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor; // NoArgsConstructor만 사용
import lombok.Setter;

/**
 * 사용자의 리뷰/게시물 '좋아요'를 나타내는 엔티티입니다.
 * (userNumber, postId) 복합 기본 키를 사용합니다.
 */
@Entity
@Table(name = "likes") // 데이터베이스 테이블 이름이 'likes'인지 확인하세요.
@Getter // Lombok: getter 메서드 자동 생성
@Setter // Lombok: setter 메서드 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성. JPA 사용 시 필수적입니다.
public class Like {

    @EmbeddedId // 복합 기본 키를 나타냅니다. LikeId 클래스를 내장합니다.
    private LikeId id;

    // ManyToOne 관계 설정: Like 엔티티는 하나의 User에 속합니다.
    @ManyToOne
    @MapsId("userNumber")
    // 'likes' 테이블의 'user_number' 컬럼이 User 엔티티를 참조합니다.
    @JoinColumn(name = "user_number")
    private User user;

    // ManyToOne 관계 설정: Like 엔티티는 하나의 Review(게시물)에 속합니다.
    @ManyToOne
    @JoinColumn(name = "post_id")
    // @MapsId는 LikeId의 'postId' 필드와 이 관계의 기본 키 부분을 매핑합니다.
    @MapsId("postId") // @MapsId 어노테이션은 ManyToOne 위에 있어야 합니다.
    private Review review; // 'Review'가 게시물(post)을 나타내는 엔티티라고 가정합니다.

    // 편의를 위한 생성자 (User와 Review 객체를 받아 복합 키 객체를 생성합니다.)
    public Like(User user, Review review) {
        this.user = user;
        this.review = review;
        // userNumber는 User 엔티티의 getUserNumber()에서, postId는 Review 엔티티의 getReviewId()에서 가져옵니다.
        this.id = new LikeId(user.getUserNumber(), review.getReviewId());
    }
}
