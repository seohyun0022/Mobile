package com.example.demo.dto;


//DTO for outgoing like responses to the client
public class LikeResponse {
 private String message; // Message for the client (e.g., "Liked", "Unliked")
 private boolean liked; // Current like status after the operation (true if liked, false if unliked)
 private Long likeCount; // Total like count for the post

 // Constructor with fields
 public LikeResponse(String message, boolean liked, Long likeCount) {
     this.message = message;
     this.liked = liked;
     this.likeCount = likeCount;
 }

 // Getters
 public String getMessage() {
     return message;
 }

 public boolean isLiked() {
     return liked;
 }

 public Long getLikeCount() {
     return likeCount;
 }

 // Setters (if mutable response is needed, but for simple DTO, getters are often enough)
 public void setMessage(String message) {
     this.message = message;
 }

 public void setLiked(boolean liked) {
     this.liked = liked;
 }

 public void setLikeCount(Long likeCount) {
     this.likeCount = likeCount;
 }
}
