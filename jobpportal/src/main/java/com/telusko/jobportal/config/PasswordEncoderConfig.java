package com.telusko.jobportal.config;

// --- IMPORT STATEMENTS ---
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// --- END OF IMPORT STATEMENTS ---

// Separate configuration for the PasswordEncoder bean
@Configuration // Marks this as a configuration class
public class PasswordEncoderConfig {

    @Bean // Defines BCryptPasswordEncoder as a Spring bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}