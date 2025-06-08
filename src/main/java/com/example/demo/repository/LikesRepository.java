package com.example.demo.repository;

import com.example.demo.entity.Like;     // Like 엔티티 import
import com.example.demo.entity.LikeId;   // LikeId 복합 키 import
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository import
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 'Like' 엔티티에 대한 데이터 접근(CRUD)을 처리하는 Repository 인터페이스입니다.
 */
@Repository
public interface LikesRepository extends JpaRepository<Like, LikeId> {

    // 특정 Review ID에 대한 좋아요 개수를 세는 메서드
    long countByReview_ReviewId(Integer reviewId); // 

    // 추가적인 쿼리 메서드가 필요하다면 여기에 정의할 수 있습니다.
}
