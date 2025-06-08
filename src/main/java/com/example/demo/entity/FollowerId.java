package com.example.demo.entity; // Follower와 같은 패키지

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Column; // jakarta.persistence 사용
import jakarta.persistence.Embeddable; // jakarta.persistence 사용
import java.io.Serializable;
import java.util.Objects;

/**
 * Embedded class defining the composite primary key for the Follower entity.
 * Must implement Serializable and override equals() and hashCode().
 * This class is now public and in its own file.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowerId implements Serializable { // <-- public으로 선언

    // serialVersionUID 추가
    private static final long serialVersionUID = 1L; 

    @Column(name = "follower_user_number")
    private int followerUserNumber;

    @Column(name = "followed_user_number")
    private int followedUserNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerId that = (FollowerId) o;
        return followerUserNumber == that.followerUserNumber &&
               followedUserNumber == that.followedUserNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerUserNumber, followedUserNumber);
    }
}
