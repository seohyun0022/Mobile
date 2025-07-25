    package com.example.demo.repository;

    import com.example.demo.entity.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.Optional;

    public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByUserNumber(int userNumber);
        Optional<User> findById(String id); // <-- This should be the method name
    }
    


