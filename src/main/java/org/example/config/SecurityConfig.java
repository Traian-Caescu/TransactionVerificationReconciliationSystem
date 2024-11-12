package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Configuration class for Spring Security settings.
 * Sets up authentication, role-based authorization, session management, and error handling.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures an in-memory user store with two users (senior and junior) with different roles.
     *
     * @return UserDetailsService that holds user information in memory.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username("senior")
                        .password("seniorpassword")
                        .roles("SENIOR")
                        .build(),
                User.withDefaultPasswordEncoder()
                        .username("junior")
                        .password("juniorpassword")
                        .roles("JUNIOR")
                        .build()
        );
    }

    /**
     * Configures security filters and settings, including CSRF disabling, authorization rules,
     * session management, and logout handling.
     *
     * @param http HttpSecurity to customize web-based security.
     * @return Configured SecurityFilterChain.
     * @throws Exception if an error occurs while configuring security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF protection for simplicity.
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll() // Publicly accessible endpoint for authentication.
                .antMatchers("/api/statistics/**").permitAll() // Publicly accessible statistics endpoint.
                .antMatchers("/data/load", "/api/reconciliation/**").hasRole("SENIOR") // SENIOR role access only.
                .antMatchers("/data/view", "/api/transactions/create").hasAnyRole("JUNIOR", "SENIOR") // JUNIOR and SENIOR roles.
                .antMatchers("/api/transactions/**").hasAnyRole("JUNIOR", "SENIOR") // JUNIOR and SENIOR roles.
                .anyRequest().authenticated() // All other requests require authentication.
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Custom entry point for unauthorized access.
                .accessDeniedHandler(accessDeniedHandler()) // Custom handler for access-denied situations.
                .and()
                .httpBasic() // Basic authentication for simplicity.
                .and()
                .sessionManagement()
                .maximumSessions(1) // Restrict each user to one session at a time.
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/login?expired=true") // Redirect URL for expired sessions.
                .and()
                .sessionFixation().migrateSession() // Session fixation protection.
                .and()
                .logout()
                .logoutUrl("/logout") // URL for logging out.
                .logoutSuccessUrl("/login?logout=true") // Redirect after successful logout.
                .deleteCookies("JSESSIONID") // Remove session cookie upon logout.
                .invalidateHttpSession(true); // Invalidate session upon logout.

        return http.build();
    }

    /**
     * Custom handler for access-denied situations. Returns HTTP 403 Forbidden with a custom message.
     *
     * @return AccessDeniedHandler that returns a forbidden response with a message.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access Denied: You do not have permission to access this resource.");
        };
    }
}
