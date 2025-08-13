package com.telusko.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // This annotation marks this as a Spring Boot application
public class JobpportalApplication { // Corrected class name typo

    public static void main(String[] args) {
        // This is the main method that gets executed when you run the application
        SpringApplication.run(JobpportalApplication.class, args); // This line starts the Spring Boot application context
    }

}