package com.example.demo.entity;

import lombok.Getter;

import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*; // Spring Boot 3.x에서는 jakarta.persistence 사용


/**
 * Follower entity maps to the 'Follower' table in MySQL.
 * It uses FollowerId class as an @Embeddable for its composite primary key.
 */
@Entity
@Table(name = "Follower")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follower {

    @EmbeddedId // Represents the composite primary key
    private FollowerId id; // Uses FollowerId class as the embedded ID

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerUserNumber") // Maps to the followerUserNumber field in FollowerId
    @JoinColumn(name = "follower_user_number", insertable = false, updatable = false) // Foreign key column
    private User followerUser; // The user who is following

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followedUserNumber") // Maps to the followedUserNumber field in FollowerId
    @JoinColumn(name = "followed_user_number", insertable = false, updatable = false) // Foreign key column
    private User followedUser; // The user who is being followed

    // Convenience constructor (can be used instead of the default constructor)
    public Follower(User followerUser, User followedUser) {
        this.followerUser = followerUser;
        this.followedUser = followedUser;
        this.id = new FollowerId(followerUser.getUserNumber(), followedUser.getUserNumber());
    }
}
