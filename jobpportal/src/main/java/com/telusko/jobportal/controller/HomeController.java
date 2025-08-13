package com.telusko.jobportal.controller;

// --- IMPORT STATEMENTS ---
import org.springframework.stereotype.Controller; // Import Controller
import org.springframework.web.bind.annotation.GetMapping; // Import GetMapping
// --- END OF IMPORT STATEMENTS ---

@Controller // Mark this as a Spring MVC Controller
public class HomeController {

    // Handles GET request to the root URL "/"
    @GetMapping("/")
    public String home() {
        return "index"; // Renders index.html
    }
}