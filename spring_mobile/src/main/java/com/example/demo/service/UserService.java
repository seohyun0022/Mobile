package com.example.demo.service;



import com.example.demo.entity.User;

import com.example.demo.repository.UserRepository;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Service class for handling user-related business logic.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user (user save/signup functionality).
     * @param request DTO containing user registration information
     * @return UserResponse DTO of the registered user
     * @throws IllegalArgumentException if user ID or email already exists
     */
    @Transactional
    public UserResponse registerUser(UserRegisterRequest request) {
        // Check for duplicate user ID
        if (userRepository.findById(request.getId()).isPresent()) {
            throw new IllegalArgumentException("User ID already exists: " + request.getId());
        }
        // Optional: Check for duplicate email
        // if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
        //     throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        // }

        User newUser = new User();
        newUser.setId(request.getId());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        newUser.setUserName(request.getUserName());
        
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            try {
                newUser.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date of birth format. Please use ISO-8601 format (YYYY-MM-DD).", e);
            }
        }
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            newUser.setGender(request.getGender().charAt(0));
        }
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(newUser);
        return new UserResponse(savedUser); // Return DTO of the registered user
    }

    /**
     * Handles user login process.
     * @param loginRequest Login request data (ID, password)
     * @return LoginResponse object (including token and user info on success)
     * @throws IllegalArgumentException on login failure
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findById(loginRequest.getId());

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found."); // User does not exist
        }

        User user = optionalUser.get();

        // Verify password match (compare with encrypted password)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password."); // Password mismatch
        }

        // On successful login, generate and return a token (dummy token here)
        // For actual JWT implementation, you would generate and return a JWT token.
        String dummyToken = "dummy_jwt_token_for_" + user.getId() + "_" + System.currentTimeMillis();

        return new LoginResponse(dummyToken, user.getId(), user.getUserName(), user.getUserNumber());
    }

    /**
     * Retrieves user information by user ID (String). (User load by ID)
     * @param id User ID to retrieve
     * @return Optional<UserResponse> object
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(UserResponse::new); // Convert User entity to UserResponse DTO
    }

    /**
     * Retrieves user information by user number (int). (User load by user_number)
     * @param userNumber User number to retrieve
     * @return Optional<UserResponse> object
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByNumber(int userNumber) {
        return userRepository.findByUserNumber(userNumber)
                .map(UserResponse::new); // Convert User entity to UserResponse DTO
    }

    /**
     * Updates user information. (User information update)
     * @param id User ID to update
     * @param updateRequest DTO containing fields to update
     * @return Updated UserResponse DTO
     * @throws IllegalArgumentException if user is not found or date format is invalid
     */
    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest updateRequest) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        User user = optionalUser.get();

        // Update only fields present and not empty in the DTO
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword())); // Encrypt new password
        }
        if (updateRequest.getUserName() != null && !updateRequest.getUserName().isEmpty()) {
            user.setUserName(updateRequest.getUserName());
        }
        if (updateRequest.getDateOfBirth() != null && !updateRequest.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(LocalDate.parse(updateRequest.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date of birth format. Please use ISO-8601 format (YYYY-MM-DD).", e);
            }
        }
        if (updateRequest.getGender() != null && !updateRequest.getGender().isEmpty()) {
            user.setGender(updateRequest.getGender().charAt(0));
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        User savedUser = userRepository.save(user); // Save the updated entity
        return new UserResponse(savedUser); // Return updated DTO
    }
}

