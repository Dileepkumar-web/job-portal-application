package com.telusko.jobportal.repository;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Application; // Import the Application entity (assuming it's in model package)
import com.telusko.jobportal.model.Job; // Import the Job entity (assuming it's in model package)
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository
import org.springframework.stereotype.Repository; // Import Repository annotation

import java.util.List; // Import List
// --- END OF IMPORT STATEMENTS ---


// Repository interface for the Application entity
@Repository // Marks this as a Spring Data JPA repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Custom method to find all Applications for a specific Job
    // Spring Data JPA implements this based on the method name and the 'job' field in the Application entity
    List<Application> findByJob(Job job);
}