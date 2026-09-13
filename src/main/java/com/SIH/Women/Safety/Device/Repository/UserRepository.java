package com.SIH.Women.Safety.Device.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SIH.Women.Safety.Device.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Email ke basis par user dhoondhne ke liye
    Optional<User> findByEmail(String email);
    
    // Username ke basis par login/search karne ke liye
    Optional<User> findByUsername(String username);
    
    // Unique Numeric ID ke basis par user ko track ya verify karne ke liye
    Optional<User> findByUniqueNumericId(String uniqueNumericId);
}