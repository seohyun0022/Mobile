package com.example.demo.dto;

//DTO for incoming like requests from the client
public class LikeRequest {
 private Long postId; // ID of the post
 private int userNumber; // User number of the user

 // Default constructor
 public LikeRequest() {
 }

 // Constructor with fields
 public LikeRequest(Long postId, int userNumber) {
     this.postId = postId;
     this.userNumber = userNumber;
 }

 // Getters and Setters
 public Long getPostId() {
     return postId;
 }

 public void setPostId(Long postId) {
     this.postId = postId;
 }

 //public int getUserNumber() {
 //    return userNumber;
 //}

 //public void setUserNumber(int userNumber) {
 //   this.userNumber = userNumber;
 //}
}
