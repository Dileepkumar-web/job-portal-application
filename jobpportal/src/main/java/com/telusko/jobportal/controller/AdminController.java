package com.telusko.jobportal.controller;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Job; // Import Job entity
import com.telusko.jobportal.repository.User; // Import User entity
import com.telusko.jobportal.model.Application; // Import Application entity
import com.telusko.jobportal.repository.ApplicationRepository; // Import ApplicationRepository
import com.telusko.jobportal.repository.JobRepository; // Import JobRepository
import com.telusko.jobportal.repository.UserRepository; // Import UserRepository
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import
import org.springframework.stereotype.Controller; // Import Controller
import org.springframework.ui.Model; // Import Model
import org.springframework.web.bind.annotation.*; // Import annotations

import java.util.List; // Import List
import java.util.Optional; // Import Optional
// --- END OF IMPORT STATEMENTS ---

@Controller // Mark this as a Spring MVC Controller
@RequestMapping("/admin") // All paths in this controller start with /admin
public class AdminController {

    // Repositories injected via constructor
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    // Constructor injection for repositories
    public AdminController(JobRepository jobRepository, UserRepository userRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    // Handles GET request for the Admin Dashboard
    // Requires ROLE_ADMIN due to SecurityConfig /admin/** rule
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get the currently authenticated admin user from the database
        User currentAdmin = getAuthenticatedUser();
        // Fetch jobs posted by this specific admin
        List<Job> jobsPostedByAdmin = jobRepository.findByPostedBy(currentAdmin);
        // Add the list of jobs to the model so the template can display them
        model.addAttribute("jobs", jobsPostedByAdmin);
        return "admin-dashboard"; // Renders admin-dashboard.html
    }

    // Handles GET request to show the form for adding a new job
    // Requires ROLE_ADMIN due to SecurityConfig /admin/** rule
    @GetMapping("/add-job")
    public String showAddJobForm(Model model) {
        // Add a new Job object to the model for form binding
        model.addAttribute("job", new Job());
        return "add-job"; // Renders add-job.html (the form)
    }

    // Handles POST request to process the new job submission
    // Requires ROLE_ADMIN due to SecurityConfig /admin/** rule
    @PostMapping("/add-job")
    public String addJob(@ModelAttribute Job job) {
        // Get the currently authenticated admin user
        User currentAdmin = getAuthenticatedUser();
        // Set the current admin as the poster of the job
        job.setPostedBy(currentAdmin);
        // Save the new job to the database
        jobRepository.save(job);
        // Redirect back to the admin dashboard after successfully adding the job
        return "redirect:/admin/dashboard";
    }

    // Handles GET request to view applications for a specific job
    // Requires ROLE_ADMIN due to SecurityConfig /admin/** rule
    @GetMapping("/view-applications/{jobId}")
    public String viewApplications(@PathVariable Long jobId, Model model) {
        // Find the job by its ID, returning Optional
        Optional<Job> jobOptional = jobRepository.findById(jobId);

        // If the job is not found, redirect to dashboard with an error parameter
        if (jobOptional.isEmpty()) {
            return "redirect:/admin/dashboard?error=jobNotFound";
        }

        // Get the Job object from the Optional
        Job job = jobOptional.get();

        // Security check: Ensure the logged-in admin is the one who posted this job
        User currentAdmin = getAuthenticatedUser();
        if (!job.getPostedBy().getId().equals(currentAdmin.getId())) {
            // If not authorized, redirect to dashboard with an unauthorized error parameter
            return "redirect:/admin/dashboard?error=unauthorized";
        }

        // Find all applications submitted for this specific job
        List<Application> applications = applicationRepository.findByJob(job);
        // Add job title and applications list to the model
        model.addAttribute("jobTitle", job.getTitle());
        model.addAttribute("applications", applications);
        return "admin-view-applications"; // Renders admin-view-applications.html
    }

    // Helper method to fetch the full User entity of the currently authenticated user from the database
    // Used by controllers to get the current logged-in user's details
    private User getAuthenticatedUser() {
        // Get the Authentication object from Spring Security's context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the username (principal name) from the Authentication object
        String username = authentication.getName();
        // Fetch the full User entity from the database using the username
        // If the user is not found in the DB (shouldn't happen for an authenticated user), throw exception
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in DB: " + username));
    }
}