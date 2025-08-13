package com.telusko.jobportal.controller;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Application; // Import Application entity
import com.telusko.jobportal.model.Job; // Import Job entity
import com.telusko.jobportal.repository.User; // Import User entity
import com.telusko.jobportal.repository.ApplicationRepository; // Import ApplicationRepository
import com.telusko.jobportal.repository.JobRepository; // Import JobRepository
import com.telusko.jobportal.repository.UserRepository; // Import UserRepository
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import
import org.springframework.stereotype.Controller; // Import Controller
import org.springframework.ui.Model; // Import Model
import org.springframework.web.bind.annotation.*; // Import annotations

import java.time.LocalDateTime; // Import LocalDateTime
import java.util.List; // Import List
import java.util.Optional; // Import Optional
// --- END OF IMPORT STATEMENTS ---

@Controller // Mark this as a Spring MVC Controller
@RequestMapping("/user") // All paths in this controller start with /user
public class UserController {

    // Repositories injected via constructor
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    // Constructor injection for repositories
    public UserController(JobRepository jobRepository, ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    // User Dashboard (simple placeholder)
    // Requires ROLE_USER due to SecurityConfig /user/** rule
    @GetMapping("/dashboard")
    public String userDashboard() {
        // Can add more user-specific info or links here later
        return "user-dashboard"; // Renders user-dashboard.html
    }

    // View all available jobs for users
    // Requires authentication due to SecurityConfig anyRequest().authenticated(), but accessible by USER role
    @GetMapping("/view-jobs")
    public String viewAllJobs(
            @RequestParam(value = "applied", required = false) String applied, // Optional applied parameter from redirect
            Model model) {
        List<Job> allJobs = jobRepository.findAll(); // Fetch all jobs from the database
        model.addAttribute("jobs", allJobs); // Add the list of jobs to the model
        // Add success message if 'applied' parameter is present (from successful application redirect)
        if (applied != null) {
            model.addAttribute("applicationSuccess", "Application submitted successfully!");
        }
        return "view-jobs"; // Renders view-jobs.html
    }

    // Show form to apply for a specific job
    // Requires authentication due to SecurityConfig anyRequest().authenticated(), but accessible by USER role
    @GetMapping("/apply/{jobId}")
    public String showApplyForm(@PathVariable Long jobId, Model model) {
        // Find the job by ID, or throw exception if not found (can add graceful handling like in AdminController)
        // For a user-facing page, graceful handling might be better than a WhiteLabel Error page.
        // Consider using Optional and checking jobOptional.isEmpty() like in AdminController's viewApplications.
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        model.addAttribute("job", job); // Add the job details to the model for display
        model.addAttribute("application", new Application()); // Add a new Application object for the form binding
        return "apply-job"; // Renders apply-job.html
    }

    // Process application submission for a job
    // Handles POST request to /user/apply/{jobId}
    // Requires authentication due to SecurityConfig anyRequest().authenticated(), but accessible by USER role
    @PostMapping("/apply/{jobId}")
    public String applyForJob(@PathVariable Long jobId, @ModelAttribute Application application) {
        // Find the job by ID again
        // Again, consider graceful handling here if jobId is invalid POSTed
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        User currentUser = getAuthenticatedUser(); // Get the currently logged-in user

        // Set relationships and timestamp for the application
        application.setJob(job);
        application.setApplicant(currentUser);
        application.setApplicationDate(LocalDateTime.now());
        // The 'coverLetter' field is automatically bound from the form via @ModelAttribute

        applicationRepository.save(application); // Save the application to the database

        // Redirect back to the jobs list with a success indicator parameter
        return "redirect:/user/view-jobs?applied";
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