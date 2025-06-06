package com.example.demo;



import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing API endpoints related to user authentication (login).
 * Handles requests from Android clients.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Lombok: Injects final fields via constructor (e.g., UserService)
// @CrossOrigin: Allows requests from specified origins (e.g., Android emulator, web apps).
// Adjust origins based on your Android app's actual IP or domain.
@CrossOrigin(origins = {"http://localhost:3000", "[http://127.0.0.1:3000](http://127.0.0.1:3000)", "http://localhost:8080", "[http://10.0.2.2:8080](http://10.0.2.2:8080)"})
public class AuthController {

    private final UserService userService;

    /**
     * Handles user login requests.
     * Receives user ID and password, attempts authentication, and returns a token on success.
     *
     * @param loginRequest DTO containing user ID and password.
     * @return ResponseEntity with LoginResponse on success (HTTP 200 OK),
     * or ErrorResponse on failure (HTTP 401 Unauthorized, 500 Internal Server Error).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Call UserService to perform login logic
            LoginResponse response = userService.login(loginRequest);
            // Return successful response with token and user details
            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (IllegalArgumentException e) {
            // Handle specific login failures (e.g., user not found, invalid password)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage())); // HTTP 401 Unauthorized
        } catch (Exception e) {
            // Catch any other unexpected server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Login failed due to server error: " + e.getMessage())); // HTTP 500 Internal Server Error
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
