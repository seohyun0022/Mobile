package com.example.demo.entity;


import jakarta.persistence.*; 

// Represents a 'like' in the database
@Entity 
@Table(name = "likes") 
public class Like {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the primary key generation strategy is IDENTITY (auto-increment)
    private Long id; // Unique identifier for the like

    @Column(nullable = false) // Specifies that the column is not nullable
    private Long postId; // ID of the post that was liked

    @Column(nullable = false)
    private int userNumber; // User number of the user who liked the post (from LoginActivity's SharedPreferences)

    public Like() {
    }

    public Like(Long postId, int userNumber) {
        this.postId = postId;
        this.userNumber = userNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }
}