package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_number")
    private int userNumber; // <--- 이 필드 이름이 'userNumber'인지 정확히 확인해주세요!

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 1)
    private Character gender;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
}
