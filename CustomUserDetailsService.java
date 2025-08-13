package com.telusko.jobportal.service;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Role; // Import Role enum
import com.telusko.jobportal.repository.User; // Import User entity
import com.telusko.jobportal.repository.UserRepository; // Import UserRepository
// Remove jakarta.annotation.PostConstruct;
// Remove org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority; // Import
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import (for creating SimpleGrantedAuthority)
import org.springframework.security.core.userdetails.UserDetails; // Import
import org.springframework.security.core.userdetails.UserDetailsService; // Import
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import (for the exception)
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service; // Import (for @Service annotation)

import java.util.ArrayList; // Import
import java.util.List; // Import List
import java.util.Optional; // Import Optional
// --- END OF IMPORT STATEMENTS ---

@Service // Mark this as a Spring service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    // No longer need @Value for default admin username/password here
    private final PasswordEncoder passwordEncoder; // Injected

    // Remove fields: adminUsername, adminPassword

    // Constructor injection for dependencies
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // *** REMOVE THE @PostConstruct createDefaultAdmin() METHOD ENTIRELY ***
    /*
    @PostConstruct
    public void createDefaultAdmin() {
       // ... (remove the code that was here) ...
    }
    */


    // This method is called by Spring Security to load user details for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find the user in the database by the provided username
        Optional<User> userOptional = userRepository.findByUsername(username);

        // If user not found, throw UsernameNotFoundException (Spring Security handles this)
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Get the User entity
        User user = userOptional.get();

        // Create authorities (roles) for the user based on their role in the database
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Add the user's role as a GrantedAuthority string (e.g., "ROLE_ADMIN", "ROLE_USER")
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        // Return a Spring Security UserDetails object
        // Spring Security will use the PasswordEncoder to compare the submitted password
        // with the encoded password returned here.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Username
                user.getPassword(), // Encoded password from DB
                authorities // User's roles/authorities
        );
    }
    // Methods related to web requests (@GetMapping, @PostMapping, getAuthenticatedUser) belong in Controllers, NOT here
}