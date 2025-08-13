package com.telusko.jobportal.model;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.repository.User; // Import the User entity (assuming it's in repository package)
import jakarta.persistence.*; // Import JPA annotations
import java.time.LocalDateTime; // Import LocalDateTime for application date
// --- END OF IMPORT STATEMENTS ---

@Entity // Marks this class as a JPA entity
@Table(name = "application") // Maps this entity to the 'application' table in the database
public class Application {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate primary key
    private Long id; // Application ID

    @ManyToOne // Many applications for One job
    @JoinColumn(name = "job_id", nullable = false) // Column in application table that links to job table
    private Job job; // The job this application is for

    @ManyToOne // Many applications by One applicant (User)
    @JoinColumn(name = "applicant_id", nullable = false) // Column in application table that links to user table
    private User applicant; // The user who submitted this application

    @Column(nullable = false) // Must not be null
    private LocalDateTime applicationDate; // Timestamp of the application

    @Column(columnDefinition = "TEXT") // Maps to a TEXT column in DB
    private String coverLetter; // Cover letter text (optional)

    // --- Getters and Setters (Add Lombok @Data or generate manually) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
}