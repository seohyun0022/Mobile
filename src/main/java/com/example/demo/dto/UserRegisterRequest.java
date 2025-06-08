package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO defining the data received from the client for a user registration request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String id;
    private String password;
    private String userName;
    private String dateOfBirth; // String to be parsed into LocalDate in service
    private String gender;
    private String email;
    private String phoneNumber;
}
