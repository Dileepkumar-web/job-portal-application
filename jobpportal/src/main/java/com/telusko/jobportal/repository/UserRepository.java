package com.telusko.jobportal.repository;

// --- IMPORT STATEMENTS ---
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.Optional; // Import Optional
// --- END OF IMPORT STATEMENTS ---

// Repository interface for the User entity
// Extends JpaRepository to get standard CRUD operations
@Repository // Marks this as a Spring Data JPA repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom method to find a User by their username
    // Spring Data JPA automatically implements this based on the method name
    Optional<User> findByUsername(String username);
}