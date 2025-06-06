package com.example.demo.repository;


import com.example.demo.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Used for handling nullable results

// JPA Repository for Like entity
@Repository // Indicates that this is a repository component
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Custom method to find a Like by userNumber and postId
    // Used to check if a user has already liked a specific post
    Optional<Like> findByUserNumberAndPostId(int userNumber, Long postId);

    // Custom method to count the number of likes for a specific postId
    Long countByPostId(Long postId);

    // Custom method to delete a like by userNumber and postId
    void deleteByUserNumberAndPostId(int userNumber, Long postId);
}