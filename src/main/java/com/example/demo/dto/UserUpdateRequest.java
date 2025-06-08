package com.example.demo.dto;


public class UserUpdateRequest {
	private String userId;
 private String password;
 private String userName;
 private String dateOfBirth; // 안드로이드에서 문자열로 넘어올 것을 고려
 private String gender;
 private String email;
 private String phoneNumber;

 // AllArgsConstructor, NoArgsConstructor, Getter, Setter, toString()
 // Lombok 사용 시 @Data, @NoArgsConstructor, @AllArgsConstructor 어노테이션으로 대체 가능

 public String getUserId() { return userId;}
 public String getPassword() { return password; }
 public void setPassword(String password) { this.password = password; }
 public String getUserName() { return userName; }
 public void setUserName(String userName) { this.userName = userName; }
 public String getDateOfBirth() { return dateOfBirth; }
 public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
 public String getGender() { return gender; }
 public void setGender(String gender) { this.gender = gender; }
 public String getEmail() { return email; }
 public void setEmail(String email) { this.email = email; }
 public String getPhoneNumber() { return phoneNumber; }
 public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
