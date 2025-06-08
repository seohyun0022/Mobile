package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter // Lombok: getter 메서드를 자동으로 생성합니다.
@Setter // Lombok: setter 메서드를 자동으로 생성합니다.
@NoArgsConstructor // Lombok: 기본 생성자를 자동으로 생성합니다.
@AllArgsConstructor // Lombok: 모든 필드를 인자로 받는 생성자를 자동으로 생성합니다.
public class LikeResponse {
    private boolean isLiked;    // 사용자가 현재 게시물에 좋아요를 눌렀는지 여부
    private long likeCount;     // 게시물의 총 좋아요 수
    private Long postId;        // 좋아요 상태를 조회하거나 변경한 게시물(리뷰)의 ID
    private Integer userNumber; // 좋아요를 누르거나 취소한 사용자의 userNumber
}
