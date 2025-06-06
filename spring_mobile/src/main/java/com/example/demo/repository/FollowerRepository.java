package com.example.demo.repository;

import com.example.demo.entity.Follower;
import com.example.demo.entity.FollowerId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JpaRepository interface responsible for database access for the Follower entity.
 * It uses FollowerId as its composite primary key.
 */
@Repository // Indicates that this interface is a Spring Data JPA repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {

    List<Follower> findByFollowedUser_UserNumber(int followedUserNumber); // 이미 수정됨

    List<Follower> findByFollowerUser_UserNumber(int followerUserNumber); // 이 부분도 수정되어야 함
}

