package com.example.demo.service;

import com.example.demo.entity.Follower;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FollowService {

    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;

    
    @Transactional(readOnly = true)
    public List<UserResponse> getFollowersOfUser(int userNumber) {
        // Check if the followed user exists
        Optional<User> followedUserOptional = userRepository.findByUserNumber(userNumber);
        if (followedUserOptional.isEmpty()) {
            throw new IllegalArgumentException("Followed user not found with userNumber: " + userNumber);
        }

        // findByFollowedUserNumber(userNumber) -> findByFollowedUser_UserNumber(userNumber)로 변경
        List<Follower> followers = followerRepository.findByFollowedUser_UserNumber(userNumber);

        // Extract follower User entities from Follower entities and convert to UserResponse DTOs
        return followers.stream()
                .map(Follower::getFollowerUser) // Get the follower User entity from Follower entity
                .map(UserResponse::new) // Convert User entity to UserResponse DTO
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public List<UserResponse> getFollowingsOfUser(int userNumber) {
        // Check if the following user exists
        Optional<User> followerUserOptional = userRepository.findByUserNumber(userNumber);
        if (followerUserOptional.isEmpty()) {
            throw new IllegalArgumentException("Following user not found with userNumber: " + userNumber);
        }

        // findByFollowerUserNumber(userNumber) -> findByFollowerUser_UserNumber(userNumber)로 변경
        List<Follower> followings = followerRepository.findByFollowerUser_UserNumber(userNumber);

        // Extract followed User entities from Follower entities and convert to UserResponse DTOs
        return followings.stream()
                .map(Follower::getFollowedUser) // Get the followed User entity from Follower entity
                .map(UserResponse::new) // Convert User entity to UserResponse DTO
                .collect(Collectors.toList());
    }

    
    @Transactional
    public Follower followUser(int followerUserNumber, int followedUserNumber) {
        if (followerUserNumber == followedUserNumber) {
            throw new IllegalArgumentException("User cannot follow themselves.");
        }

        Optional<User> followerUserOptional = userRepository.findByUserNumber(followerUserNumber);
        Optional<User> followedUserOptional = userRepository.findByUserNumber(followedUserNumber);

        if (followerUserOptional.isEmpty()) {
            throw new IllegalArgumentException("Follower user not found with userNumber: " + followerUserNumber);
        }
        if (followedUserOptional.isEmpty()) {
            throw new IllegalArgumentException("Followed user not found with userNumber: " + followedUserNumber);
        }

        User followerUser = followerUserOptional.get();
        User followedUser = followedUserOptional.get();

        // Check if already following
        if (followerRepository.findById(new com.example.demo.entity.FollowerId(followerUserNumber, followedUserNumber)).isPresent()) {
            throw new IllegalArgumentException("Already following this user.");
        }

        Follower follower = new Follower(followerUser, followedUser);
        return followerRepository.save(follower);
    }

    
    @Transactional
    public void unfollowUser(int followerUserNumber, int followedUserNumber) {
        com.example.demo.entity.FollowerId id = new com.example.demo.entity.FollowerId(followerUserNumber, followedUserNumber);
        if (followerRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Not following this user.");
        }
        followerRepository.deleteById(id);
    }
}
