package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.entity.Review;
import com.example.demo.entity.Follower;
import com.example.demo.service.UserService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.FollowService;
import com.example.demo.service.LikesService; // LikesService import 추가
import com.example.demo.repository.FollowerRepository;
import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.LikeRequest; // LikeRequest DTO import 추가
import com.example.demo.dto.ReviewResponse; // ReviewResponse import 추가 (review1, review2 변수 타입 변경)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Spring Boot 애플리케이션의 메인 클래스입니다.
 * 애플리케이션 시작 시 필요한 초기 설정을 수행하고, 테스트 데이터를 삽입합니다.
 */
@SpringBootApplication
public class SpringMobileApplication {

    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private FollowService followService;
    @Autowired // LikesService 주입 추가
    private LikesService likesService;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringMobileApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            System.out.println("--- Starting data initialization ---");

            // 1. 테스트 사용자 등록
            User user1 = null;
            UserRegisterRequest registerRequest1 = new UserRegisterRequest(
                    "testuser1", "password123", "테스트유저1", "1990-01-01", "M", "test1@example.com", "010-1111-2222"
            );
            try {
                user1 = userService.registerUser(registerRequest1).toEntity();
                System.out.println("Test user1 registered successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                user1 = userService.getUserById("testuser1").map(u -> {
                    User entity = new User();
                    entity.setUserNumber(u.getUserNumber());
                    entity.setId(u.getId());
                    entity.setUserName(u.getUserName());
                    entity.setDateOfBirth(u.getDateOfBirth());
                    entity.setGender(u.getGender());
                    entity.setEmail(u.getEmail());
                    entity.setPhoneNumber(u.getPhoneNumber());
                    return entity;
                }).orElse(null);
                if (user1 != null) {
                    System.out.println("Test user1 already exists, retrieved from DB.");
                }
            }

            User user2 = null;
            UserRegisterRequest registerRequest2 = new UserRegisterRequest(
                    "testuser2", "password456", "테스트유저2", "1995-05-10", "F", "test2@example.com", "010-3333-4444"
            );
            try {
                user2 = userService.registerUser(registerRequest2).toEntity();
                System.out.println("Test user2 registered successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                user2 = userService.getUserById("testuser2").map(u -> {
                    User entity = new User();
                    entity.setUserNumber(u.getUserNumber());
                    entity.setId(u.getId());
                    entity.setUserName(u.getUserName());
                    entity.setDateOfBirth(u.getDateOfBirth());
                    entity.setGender(u.getGender());
                    entity.setEmail(u.getEmail());
                    entity.setPhoneNumber(u.getPhoneNumber());
                    return entity;
                }).orElse(null);
                if (user2 != null) {
                    System.out.println("Test user2 already exists, retrieved from DB.");
                }
            }

            User user3 = null;
            UserRegisterRequest registerRequest3 = new UserRegisterRequest(
                    "testuser3", "password789", "테스트유저3", "2000-03-15", "M", "test3@example.com", "010-5555-6666"
            );
            try {
                user3 = userService.registerUser(registerRequest3).toEntity();
                System.out.println("Test user3 registered successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                user3 = userService.getUserById("testuser3").map(u -> {
                    User entity = new User();
                    entity.setUserNumber(u.getUserNumber());
                    entity.setId(u.getId());
                    entity.setUserName(u.getUserName());
                    entity.setDateOfBirth(u.getDateOfBirth());
                    entity.setGender(u.getGender());
                    entity.setEmail(u.getEmail());
                    entity.setPhoneNumber(u.getPhoneNumber());
                    return entity;
                }).orElse(null);
                if (user3 != null) {
                    System.out.println("Test user3 already exists, retrieved from DB.");
                }
            }

            // 2. 테스트 리뷰 등록
            ReviewResponse review1Response = null; // Review -> ReviewResponse로 타입 변경
            ReviewResponse review2Response = null; // Review -> ReviewResponse로 타입 변경

            if (user1 != null) {
            	ReviewCreateRequest req1 = new ReviewCreateRequest(
            		    user1.getUserNumber(),
            		    "피자맛집",
            		    new BigDecimal("37.5670135"),
            		    new BigDecimal("126.978147"),
            		    "http://example.com/pizza.jpg",
            		    new BigDecimal("4.5"), // Double -> BigDecimal로 수정
            		    "최고의 피자!",
            		    "여기 피자는 정말 환상적입니다. 꼭 드셔보세요!"
            		);
                review1Response = reviewService.createReview(req1);
                System.out.println("Review 1 created by " + user1.getUserName() + " (Review ID: " + review1Response.getReviewId() + ")");
            }

            if (user2 != null) {
                ReviewCreateRequest req2 = new ReviewCreateRequest(
                        user2.getUserNumber(),
                        "커피향가득",
                        new BigDecimal("37.5000000"),
                        new BigDecimal("127.0500000"),
                        null,
                        new BigDecimal("3.8"), // Double -> BigDecimal로 수정
                        "분위기 좋은 카페",
                        "커피 맛도 좋고, 조용해서 작업하기 좋아요."
                );
                review2Response = reviewService.createReview(req2);
                System.out.println("Review 2 created by " + user2.getUserName() + " (Review ID: " + review2Response.getReviewId() + ")");
            }

            // 3. 테스트 팔로우 관계 등록
            if (user1 != null && user2 != null) {
                try {
                    followService.followUser(user1.getUserNumber(), user2.getUserNumber());
                    System.out.println(user1.getUserName() + " followed " + user2.getUserName());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (user3 != null && user2 != null) {
                try {
                    followService.followUser(user3.getUserNumber(), user2.getUserNumber());
                    System.out.println(user3.getUserName() + " followed " + user2.getUserName());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (user1 != null && user3 != null) {
                try {
                    followService.followUser(user1.getUserNumber(), user3.getUserNumber());
                    System.out.println(user1.getUserName() + " followed " + user3.getUserName());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            // 4. 테스트 좋아요 관계 등록 (새로 추가된 부분 주석 해제 및 수정)
            if (user1 != null && review2Response != null) { // user1이 review2에 좋아요
                try {
                    likesService.toggleLike(review2Response.getReviewId().longValue(), user1.getUserNumber()); // ReviewId를 Long으로 변환
                    System.out.println(user1.getUserName() + " liked Review ID: " + review2Response.getReviewId());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (user3 != null && review1Response != null) { // user3이 review1에 좋아요
                try {
                    likesService.toggleLike(review1Response.getReviewId().longValue(), user3.getUserNumber()); // ReviewId를 Long으로 변환
                    System.out.println(user3.getUserName() + " liked Review ID: " + review1Response.getReviewId());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("--- Data initialization complete ---");
        };
    }
}
