package com.telusko.jobportal.model;

// Enum to define the roles a user can have in the application
// Spring Security typically expects roles to start with "ROLE_"
public enum Role {
    ROLE_ADMIN,
    ROLE_USER
}