package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JpaRepository interface responsible for database access for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 이 메서드가 반드시 있어야 합니다.
    Optional<User> findById(String id); // 사용자 ID(String)로 조회

    Optional<User> findByUserNumber(int userNumber); // 사용자 번호(int)로 조회
}


