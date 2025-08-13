package com.telusko.jobportal.repository;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Job; // Import the Job entity (assuming it's in model package)
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.List; // Import List
// --- END OF IMPORT STATEMENTS ---


// Repository interface for the Job entity
@Repository // Marks this as a Spring Data JPA repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Custom method to find all Jobs posted by a specific User
    // Spring Data JPA implements this based on the method name and the 'postedBy' field in the Job entity
    List<Job> findByPostedBy(User postedBy);
}