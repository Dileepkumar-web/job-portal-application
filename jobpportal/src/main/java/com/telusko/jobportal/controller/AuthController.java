package com.telusko.jobportal.controller;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Role; // Import Role enum
import com.telusko.jobportal.repository.User; // Import User entity
import com.telusko.jobportal.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Value; // Import Value annotation
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Controller; // Import Controller
import org.springframework.ui.Model; // Import Model
import org.springframework.web.bind.annotation.GetMapping; // Import GetMapping
import org.springframework.web.bind.annotation.ModelAttribute; // Import ModelAttribute
import org.springframework.web.bind.annotation.PostMapping; // Import PostMapping
import org.springframework.web.bind.annotation.RequestParam; // Import RequestParam
// --- END OF IMPORT STATEMENTS ---

@Controller // Mark this as a Spring MVC Controller
public class AuthController {

    // Dependencies injected via constructor
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject login page paths from application properties using @Value
    @Value("${security.admin.login-page}")
    private String adminLoginPage;

    @Value("${security.user.login-page}")
    private String userLoginPage;

    // Constructor injection
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Handles GET request to show the Admin Login Page
    // Catches 'error' parameter from failed logins
    // Accessible by everyone due to SecurityConfig permitAll()
    @GetMapping("${security.admin.login-page}")
    public String showAdminLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        // If the 'error' parameter is present, add a login error message to the model
        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password for Admin.");
        }
        return "login-admin"; // Renders login-admin.html
    }

    // Handles GET request to show the User Login Page
    // Catches 'error' parameter from failed logins and 'registered' from successful registration redirects
    // Accessible by everyone due to SecurityConfig permitAll()
    @GetMapping("${security.user.login-page}")
    public String showUserLoginPage(
            @RequestParam(value = "error", required = false) String error, // Optional error parameter
            @RequestParam(value = "registered", required = false) String registered, // Optional registered parameter
            Model model) {
        // If the 'error' parameter is present, add a login error message to the model
        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password for User.");
        }
        // If the 'registered' parameter is present, add a registration success message to the model
        if (registered != null) {
            model.addAttribute("registrationSuccess", "Registration successful! Please log in.");
        }
        return "login-user"; // Renders login-user.html
    }

    // Handles GET request to show the User Registration Page
    // Accessible by everyone due to SecurityConfig permitAll()
    @GetMapping("/register-user")
    public String showUserRegistrationForm(
            @RequestParam(value = "error", required = false) String error, // Optional error parameter
            Model model) {
        model.addAttribute("user", new User()); // Add a new User object for form binding
        // If the 'error' parameter is present and is "exists", add a username exists message
        if (error != null && error.equals("exists")) {
            model.addAttribute("registrationError", "Username already exists.");
        }
        return "register-user"; // Renders register-user.html
    }

    // Handles POST request to process User Registration
    // Accessible by everyone due to SecurityConfig permitAll()
    @PostMapping("/register-user")
    public String registerUser(@ModelAttribute User user) {
        // Check if a user with the submitted username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // If username exists, redirect back to the user registration page with an error parameter
            return "redirect:/register-user?error=exists";
        }

        // Encode the submitted password using the PasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Assign the default role for new registrations (USER)
        user.setRole(Role.ROLE_USER);

        // Save the new user entity to the database
        userRepository.save(user);

        // Redirect to the user login page with a success indicator parameter
        return "redirect:/login-user?registered";
    }

    // Handles GET request to show the Admin Registration Page
    // Accessible by everyone due to SecurityConfig permitAll()
    @GetMapping("/register-admin")
    public String showAdminRegistrationForm(
            @RequestParam(value = "error", required = false) String error, // Optional error parameter
            Model model) {
        model.addAttribute("user", new User()); // Add a new User object for form binding
        // If the 'error' parameter is present and is "exists", add a username exists message
        if (error != null && error.equals("exists")) {
            model.addAttribute("registrationError", "Username already exists.");
        }
        return "register-admin"; // Renders the new register-admin.html
    }

    // Handles POST request to process Admin Registration
    // Accessible by everyone due to SecurityConfig permitAll()
    @PostMapping("/register-admin")
    public String registerAdmin(@ModelAttribute User user) {
        // Check if a user with the submitted username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // If username exists, redirect back to the admin registration page with an error parameter
            return "redirect:/register-admin?error=exists";
        }

        // Encode the submitted password using the PasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Assign the ADMIN role for new admin registrations
        user.setRole(Role.ROLE_ADMIN); // *** Assign ADMIN role ***

        // Save the new admin user entity to the database
        userRepository.save(user);

        // *** THIS LINE CONTROLS THE REDIRECT AFTER SUCCESSFUL ADMIN REGISTRATION ***
        return "redirect:/login-admin?registered"; // *** This tells Spring to redirect to /login-admin ***
    }

    // Spring Security's formLogin configuration handles the POST requests to
    // the common processing URL (/do-login) automatically.
    // You DO NOT need explicit @PostMapping methods for /do-login in this controller.
}