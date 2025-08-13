package com.telusko.jobportal.repository; // Placing User entity here based on controller imports

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Role; // Import your Role enum
import jakarta.persistence.*; // Import JPA annotations
// --- END OF IMPORT STATEMENTS ---


@Entity // Marks this class as a JPA entity
@Table(name = "user") // Maps this entity to the 'user' table in the database
public class User {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the primary key value
    private Long id; // Primary key

    @Column(nullable = false, unique = true) // Maps to the 'username' column, must not be null and must be unique
    private String username; // User's username

    @Column(nullable = false) // Maps to the 'password' column, must not be null
    private String password; // User's encoded password

    @Enumerated(EnumType.STRING) // Maps to the 'role' column, store enum name as a string
    @Column(nullable = false) // Must not be null
    private Role role; // User's role (ADMIN or USER)

    // --- Getters and Setters (Add Lombok @Data or generate manually) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}