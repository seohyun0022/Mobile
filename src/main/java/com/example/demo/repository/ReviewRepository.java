package com.example.demo.repository;

import com.example.demo.entity.Review;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * ReviewRepository는 Review 엔티티에 대한 데이터베이스 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 제공합니다.
 * 또한, 특정 User와 관련된 리뷰를 조회하는 사용자 정의 메서드를 포함합니다.
 */
@Repository // 이 인터페이스가 데이터 접근 계층의 컴포넌트임을 나타냅니다.
public interface ReviewRepository extends JpaRepository<Review, Integer> {

   
    List<Review> findByUser(User user);

}

