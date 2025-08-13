package com.telusko.jobportal.config;

// --- IMPORT STATEMENTS ---
import com.telusko.jobportal.model.Role; // Import your Role enum
import com.telusko.jobportal.service.CustomUserDetailsService; // Import your UserDetailsService
import org.springframework.context.annotation.Bean; // Import
import org.springframework.context.annotation.Configuration; // Import
import org.springframework.http.HttpMethod; // Import HttpMethod (if used for specific matchers)
import org.springframework.security.authentication.AuthenticationManager; // Import
import org.springframework.security.authentication.ProviderManager; // Import
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Import
import org.springframework.security.crypto.password.PasswordEncoder; // Import
import org.springframework.security.web.SecurityFilterChain; // Import
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; // Import handler interface
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Import for logout matcher
// --- END OF IMPORT STATEMENTS ---


@Configuration // Marks this as a configuration class
@EnableWebSecurity // Enables Spring Security's web security features
public class SecurityConfig {

    // Static variables for login and logout URLs (match application.properties)
    private static final String ADMIN_LOGIN_PAGE = "/login-admin"; // GET URL for admin login form
    private static final String USER_LOGIN_PAGE = "/login-user"; // GET URL for user login form

    // *** Use a single, common processing URL for ALL logins (Admin and User) ***
    // Both login forms will submit their POST request to this URL
    private static final String COMMON_LOGIN_PROCESSING_URL = "/do-login";

    // Keep a common failure URL for redirects on failed login attempts
    // We'll redirect all failed logins here (AuthController needs to handle 'error' param)
    private static final String LOGIN_FAILURE_URL = "/login-user?error";


    private static final String LOGOUT_URL = "/logout"; // URL for logout POST request
    private static final String LOGOUT_SUCCESS_URL = "/"; // Redirect after successful logout (home page)

    // Dependencies injected by Spring
    private final CustomUserDetailsService customUserDetailsService; // Your service to load user details
    private final PasswordEncoder passwordEncoder; // Your password encoder bean (from PasswordEncoderConfig)
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler; // Your custom handler for redirects

    // Constructor injection
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    // Defines the security filter chain that handles requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // --- Authorization Rules ---
                .authorizeHttpRequests(authorize -> authorize
                        // Permit access to public pages (home, register, static resources)
                        .requestMatchers("/", "/register", "/css/**", "/js/**").permitAll()
                        // Permit access to the login pages (GET) and the COMMON processing URL (POST)
                        // Spring Security's formLogin handles the actual authentication processing at the COMMON URL
                        .requestMatchers(ADMIN_LOGIN_PAGE, USER_LOGIN_PAGE, COMMON_LOGIN_PROCESSING_URL).permitAll()

                        // Require ADMIN role for any URL starting with /admin/
                        // hasRole("ADMIN") automatically checks for authority "ROLE_ADMIN" provided by UserDetails
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Require USER role for any URL starting with /user/
                        .requestMatchers("/user/**").hasRole("USER")

                        // Any other request requires authentication (handled by standard formLogin)
                        .anyRequest().authenticated()
                )
                // --- Configure a SINGLE Form Login process ---
                // This single configuration handles POST requests to COMMON_LOGIN_PROCESSING_URL from both forms
                .formLogin(formLogin -> formLogin
                        // Specify the default login page Spring Security will redirect to if authentication is required
                        // We point this to the user login page as a general fallback/entry point
                        .loginPage(USER_LOGIN_PAGE)
                        // *** Specify the single URL that ALL login forms will POST to for processing ***
                        .loginProcessingUrl(COMMON_LOGIN_PROCESSING_URL)
                        // *** Use the custom success handler to determine the redirect based on role AFTER successful authentication ***
                        .successHandler(customAuthenticationSuccessHandler)
                        // Redirect URL on authentication FAILURE
                        .failureUrl(LOGIN_FAILURE_URL)
                        .permitAll() // IMPORTANT: Allow everyone to access the configured login page(s) and the processing URL
                )
                // --- Logout Configuration ---
                .logout(logout -> logout
                        // Configure the logout URL and method (POST is standard for logout)
                        .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL, "POST"))
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL) // Redirect URL after successful logout (home page)
                        .permitAll() // Allow everyone to access the logout URL
                );
        // Optional: CSRF configuration (enabled by default, needed for POST forms without JS unless disabled)
        // If you use Thymeleaf, th:action and th:object forms automatically include CSRF tokens if csrf is enabled.
        // Ensure your plain HTML forms for login include the hidden CSRF token input.
        // .csrf(csrf -> csrf.disable()); // Not recommended for production, only for quick testing if absolutely necessary

        // Build the SecurityFilterChain
        return http.build();
    }

    // Authentication Manager bean - tells Spring Security how to authenticate users
    // Remains the same, uses your CustomUserDetailsService and PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // Use your service to load users
        provider.setPasswordEncoder(passwordEncoder); // Use your password encoder
        return new ProviderManager(provider); // Manages authentication providers
    }

    // Note: PasswordEncoder bean should be in PasswordEncoderConfig.java
    // Note: CustomAuthenticationSuccessHandler is a separate @Component bean (code provided above)
}