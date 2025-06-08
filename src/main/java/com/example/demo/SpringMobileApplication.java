package com.example.demo;

import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LikesService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import com.example.demo.entity.*;
import com.example.demo.dto.*;
import com.example.demo.service.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import com.example.demo.dto.LikeResponse; // LikeResponse DTO import 추가

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class SpringMobileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMobileApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            ApplicationContext applicationContext,
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            LikesService likesService) {
        return args -> {
            UserService userService = applicationContext.getBean(UserService.class);

            // 사용자 변수를 람다 스코프 최상단에 선언하여 모든 블록에서 접근 가능하도록 합니다.
            User user1 = null;
            User user2 = null;
            User user3 = null;
            User user4 = null;
            User user5 = null;

            // 초기 사용자 데이터 생성
            try {
                UserRegisterRequest user1Req = new UserRegisterRequest("testuser1", "password123", "테스트유저1", "1990-01-01", "M", "test1@example.com", "010-1111-2222");
                userService.registerUser(user1Req);
                System.out.println("User testuser1 registered.");

                UserRegisterRequest user2Req = new UserRegisterRequest("testuser2", "password123", "테스트유저2", "1992-02-02", "F", "test2@example.com", "010-3333-4444");
                userService.registerUser(user2Req);
                System.out.println("User testuser2 registered.");

                UserRegisterRequest user3Req = new UserRegisterRequest("testuser3", "password123", "테스트유저3", "1993-03-03", "M", "test3@example.com", "010-5555-6666");
                userService.registerUser(user3Req);
                System.out.println("User testuser3 registered.");

                UserRegisterRequest user4Req = new UserRegisterRequest("testuser4", "password123", "테스트유저4", "1994-04-04", "F", "test4@example.com", "010-7777-8888");
                userService.registerUser(user4Req);
                System.out.println("User testuser4 registered.");

                UserRegisterRequest user5Req = new UserRegisterRequest("testuser5", "password123", "서현호", "1995-05-05", "M", "test5@example.com", "010-9999-0000");
                userService.registerUser(user5Req);
                System.out.println("User testuser5 registered.");

            } catch (IllegalArgumentException e) {
                System.out.println("User registration skipped: " + e.getMessage());
            }

            // 사용자 등록 또는 조회 후, userNumber를 기반으로 User 엔티티를 다시 조회하여 변수에 할당합니다.
            // 이렇게 하면 user1, user2, user3 등이 초기화된 상태로 다음 로직에 사용될 수 있습니다.
            user1 = userRepository.findByUserNumber(1).orElse(null);
            user2 = userRepository.findByUserNumber(2).orElse(null);
            user3 = userRepository.findByUserNumber(3).orElse(null);
            user4 = userRepository.findByUserNumber(4).orElse(null);
            user5 = userRepository.findByUserNumber(5).orElse(null);


            // 초기 리뷰 데이터 생성 (userNumber 1, 2, 3 사용)
            Review review1 = null; // review1 변수 선언
            Review review2 = null; // review2 변수 선언
            Review review3 = null; // review3 변수 선언

            if (reviewRepository.count() == 0) { // 리뷰가 없을 때만 초기 데이터 생성
                if (user1 != null) {
                    Review newReview1 = new Review(); // 새로운 Review 객체 생성
                    newReview1.setUser(user1);
                    newReview1.setStoreName("Cafe Wonderland");
                    newReview1.setLatitude(new BigDecimal("37.567890"));
                    newReview1.setLongitude(new BigDecimal("126.987654"));
                    newReview1.setImagePath("http://example.com/cafe1.jpg");
                    newReview1.setTitle("아늑하고 커피 맛집!");
                    newReview1.setBody("따뜻한 분위기에서 즐거운 시간을 보냈습니다. 라떼가 최고예요.");
                    newReview1.setRating(new BigDecimal("4.5"));
                    review1 = reviewRepository.save(newReview1); // 저장 후 반환된 엔티티를 review1 변수에 할당
                    System.out.println("Review 1 saved (ID: " + review1.getReviewId() + ").");
                }

                if (user2 != null) {
                    Review newReview2 = new Review(); // 새로운 Review 객체 생성
                    newReview2.setUser(user2);
                    newReview2.setStoreName("Restaurant Flavors");
                    newReview2.setLatitude(new BigDecimal("37.555555"));
                    newReview2.setLongitude(new BigDecimal("126.999999"));
                    newReview2.setImagePath("http://example.com/restaurant1.jpg");
                    newReview2.setTitle("다양한 메뉴, 만족스러운 식사!");
                    newReview2.setBody("친구들과 함께 방문했는데, 모든 메뉴가 맛있고 서비스도 좋았습니다.");
                    newReview2.setRating(new BigDecimal("4.0"));
                    review2 = reviewRepository.save(newReview2); // 저장 후 반환된 엔티티를 review2 변수에 할당
                    System.out.println("Review 2 saved (ID: " + review2.getReviewId() + ").");
                }

                if (user3 != null) {
                    Review newReview3 = new Review(); // 새로운 Review 객체 생성
                    newReview3.setUser(user3);
                    newReview3.setStoreName("Bookstore Haven");
                    newReview3.setLatitude(new BigDecimal("37.543210"));
                    newReview3.setLongitude(new BigDecimal("126.976543"));
                    newReview3.setImagePath("http://example.com/bookstore1.jpg");
                    newReview3.setTitle("책과 커피의 조화!");
                    newReview3.setBody("조용히 책을 읽고 싶을 때 방문하기 좋습니다. 다양한 서적과 편안한 공간.");
                    newReview3.setRating(new BigDecimal("4.8"));
                    review3 = reviewRepository.save(newReview3); // 저장 후 반환된 엔티티를 review3 변수에 할당
                    System.out.println("Review 3 saved (ID: " + review3.getReviewId() + ").");
                }
            } else { // 리뷰가 이미 존재하는 경우, 기존 리뷰를 조회하여 변수에 할당
                review1 = reviewRepository.findById(1).orElse(null);
                review2 = reviewRepository.findById(2).orElse(null);
                review3 = reviewRepository.findById(3).orElse(null);
                System.out.println("Reviews already exist, skipping creation and retrieving from DB.");
            }


            // 좋아요 데이터 생성 (testuser1이 review1, review2에 좋아요)
            try {
                // review1과 review2 변수는 이제 상위 스코프에서 선언되고 할당되므로 직접 사용 가능합니다.
                // 그러나 null 체크는 여전히 중요합니다.
                if (user1 != null && review2 != null) { // user1이 review2에 좋아요
                    LikeResponse response = likesService.toggleLike(review2.getReviewId().longValue(), user1.getUserNumber());
                    System.out.println(user1.getUserName() + " liked Review ID: " + review2.getReviewId() + ". Is liked: " + response.isLiked() + ", Total likes: " + response.getLikeCount());
                }
                if (user3 != null && review1 != null) { // user3이 review1에 좋아요
                    LikeResponse response = likesService.toggleLike(review1.getReviewId().longValue(), user3.getUserNumber());
                    System.out.println(user3.getUserName() + " liked Review ID: " + review1.getReviewId() + ". Is liked: " + response.isLiked() + ", Total likes: " + response.getLikeCount());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Like toggling skipped: " + e.getMessage());
            }

            // 추가: 팔로우 데이터 생성 (기존 로직 그대로 유지)
            // 팔로우 부분도 user1, user2, user3이 null이 아닐 때만 실행되도록 변경하면 더 안전합니다.
            if (user1 != null && user2 != null) {
                try {
                    // 팔로우 서비스는 여기서 주입받지 않았으므로 applicationContext.getBean()을 사용하거나,
                    // CommandLineRunner의 인자로 followService를 주입받아야 합니다.
                    // 현재 코드에서 followService는 CommandLineRunner의 인자로 주입받고 있으므로 그대로 사용합니다.
                    likesService.toggleLike(review2.getReviewId().longValue(), user1.getUserNumber()); // ReviewId를 Long으로 변환
                    System.out.println(user1.getUserName() + " liked Review ID: " + review2.getReviewId());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (user3 != null && review1 != null) { // user3이 review1에 좋아요
                try {
                    likesService.toggleLike(review1.getReviewId().longValue(), user3.getUserNumber()); // ReviewId를 Long으로 변환
                    System.out.println(user3.getUserName() + " liked Review ID: " + review1.getReviewId());
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }


            System.out.println("--- Data initialization complete ---");
        };
    }
}
