package com.telusko.jobportal.model;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.repository.User; // Import the User entity (assuming it's in repository package)
import jakarta.persistence.*; // Import JPA annotations
// --- END OF IMPORT STATEMENTS ---

@Entity // Marks this class as a JPA entity
@Table(name = "job") // Maps this entity to the 'job' table in the database
public class Job {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate primary key
    private Long id; // Job ID

    @Column(nullable = false) // Must not be null
    private String title; // Job title

    @Column(columnDefinition = "TEXT") // Maps to a TEXT column in DB
    private String description; // Job description

    private String location; // Job location

    @ManyToOne // Many jobs can be posted by One user
    @JoinColumn(name = "posted_by_id", nullable = false) // Column in job table that links to user table
    private User postedBy; // The user who posted this job

    // --- Getters and Setters (Add Lombok @Data or generate manually) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }
}