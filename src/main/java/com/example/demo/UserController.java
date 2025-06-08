package com.example.demo;

import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.dto.FollowResponse; // Added FollowResponse DTO
import com.example.demo.service.UserService;
import com.example.demo.service.FollowService; // Added FollowService
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

/**
 * Controller providing API endpoints related to user profile retrieval, update, registration,
 * and follower/following list retrieval.
 * Handles requests from Android clients.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Lombok: Injects final fields via constructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:8080", "http://10.0.2.2:8080"})
public class UserController {

    private final UserService userService;
    private final FollowService followService; // Inject FollowService

    /**
     * Registers a new user (user save/signup functionality).
     *
     * @param request DTO containing user registration information.
     * @return ResponseEntity with UserResponse DTO on success (HTTP 201 Created),
     * or ErrorResponse on failure (HTTP 409 Conflict, 500 Internal Server Error).
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            UserResponse registeredUser = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser); // HTTP 201 Created
        } catch (IllegalArgumentException e) {
            // Handle cases like duplicate user ID or invalid data
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage())); // HTTP 409 Conflict
        } catch (Exception e) {
            // Catch any other unexpected server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

     
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<UserResponse> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get()); // HTTP 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found with ID: " + id)); // HTTP 404 Not Found
        }
    }

    
    @GetMapping("/number/{userNumber}")
    public ResponseEntity<?> getUserByNumber(@PathVariable int userNumber) {
        Optional<UserResponse> userOptional = userService.getUserByNumber(userNumber);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get()); // HTTP 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found with userNumber: " + userNumber)); // HTTP 404 Not Found
        }
    }

    /**
     * Updates user profile information for a specific user ID. (User information update)
     * Uses PATCH for partial updates, allowing clients to send only the fields they want to change.
     *
     * @param id User ID to update.
     * @param updateRequest DTO containing fields to update.
     * @return ResponseEntity with updated UserResponse DTO on success (HTTP 200 OK),
     * or ErrorResponse on failure (HTTP 404 Not Found, 500 Internal Server Error).
     */
    @PatchMapping("/id/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest updateRequest) {
        try {
            UserResponse updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(updatedUser); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            // Handle cases like user not found or invalid data format
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // HTTP 404 Not Found
        } catch (Exception e) {
            // Catch any other unexpected server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to update user: " + e.getMessage())); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Retrieves a list of all followers for a specific user. (Get names of users who followed me)
     *
     * @param userNumber The user number of the user whose followers are to be retrieved (the "followed" user).
     * @return ResponseEntity with FollowResponse DTO containing a list of UserResponse DTOs on success (HTTP 200 OK),
     * or ErrorResponse on failure (HTTP 404 Not Found, 500 Internal Server Error).
     */
    @GetMapping("/{userNumber}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable int userNumber) {
        try {
            List<UserResponse> followers = followService.getFollowersOfUser(userNumber);
            // Wrap the list in a FollowResponse DTO to include count and a clear structure
            return ResponseEntity.ok(new FollowResponse(followers, followers.size())); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // HTTP 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to retrieve followers: " + e.getMessage())); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Retrieves a list of all users that a specific user is following. (Get names of users I followed)
     *
     * @param userNumber The user number of the user who is following (the "follower" user).
     * @return ResponseEntity with FollowResponse DTO containing a list of UserResponse DTOs on success (HTTP 200 OK),
     * or ErrorResponse on failure (HTTP 404 Not Found, 500 Internal Server Error).
     */
    @GetMapping("/{userNumber}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable int userNumber) {
        try {
            List<UserResponse> followings = followService.getFollowingsOfUser(userNumber);
            // Wrap the list in a FollowResponse DTO to include count and a clear structure
            return ResponseEntity.ok(new FollowResponse(followings, followings.size())); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // HTTP 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to retrieve followings: " + e.getMessage())); // HTTP 500 Internal Server Error
        }
    }

    /**
     * Simple DTO for consistent error responses.
     * Contains a message describing the error.
     */
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
