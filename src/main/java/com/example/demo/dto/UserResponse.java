package com.example.demo.dto;

import com.example.demo.entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 사용자 정보를 클라이언트에 반환할 때 사용하는 DTO입니다.
 * 보안을 위해 비밀번호는 포함하지 않습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private int userNumber;
    private String id;
    private String userName;
    private LocalDate dateOfBirth;
    private Character gender;
    private String email;
    private String phoneNumber;

   
    public UserResponse(User user) {
        this.userNumber = user.getUserNumber();
        this.id = user.getId();
        this.userName = user.getUserName();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }

   
    public User toEntity() {
        User user = new User();
        user.setUserNumber(this.userNumber);
        user.setId(this.id);
        user.setUserName(this.userName);
        user.setDateOfBirth(this.dateOfBirth);
        user.setGender(this.gender);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        // password 필드는 DTO에 없으므로 설정하지 않음 (DB에 저장 시 암호화된 비밀번호가 필요)
        return user;
    }
}
