    package com.example.demo.entity;

    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;

    import java.time.LocalDate;

    @Entity
    @Table(name = "User") // Assumed table name
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_number") // Primary key for the User entity
        private Integer userNumber;

        @Column(name = "id", unique = true, nullable = false, length = 50) // User's login ID (String), unique
        private String id;

        @Column(name = "user_name", nullable = false, length = 100)
        private String userName;

        @Column(name = "date_of_birth")
        private LocalDate dateOfBirth;

        @Column(name = "gender", length = 1)
        private Character gender;

        @Column(name = "email", unique = true, length = 100)
        private String email;

        @Column(name = "phone_number", length = 20)
        private String phoneNumber;

        @Column(name = "password", nullable = false) // Assuming password field exists in entity
        private String password;

        public User() {}

        // Constructor (adjust based on your User creation flow)
        public User(String id, String userName, LocalDate dateOfBirth, Character gender, String email, String phoneNumber, String password) {
            this.id = id;
            this.userName = userName;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
        }

        // Getters and Setters
        public Integer getUserNumber() { return userNumber; }
        public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }

        public String getId() { return id; } // Crucial: ensure this method exists
        public void setId(String id) { this.id = id; } // Crucial: ensure this method exists

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public Character getGender() { return gender; }
        public void setGender(Character gender) { this.gender = gender; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    