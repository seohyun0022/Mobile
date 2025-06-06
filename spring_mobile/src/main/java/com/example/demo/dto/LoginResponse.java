package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO defining the data returned to the client upon successful login.
 * Sensitive information like passwords should not be included in this DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;     // Authentication token (e.g., JWT)
    private String userId;    // Unique user ID
    private String username;  // User's display name
    private int userNumber;   // User's primary key from DB
}

