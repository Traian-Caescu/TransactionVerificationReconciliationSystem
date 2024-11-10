package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("adminpassword")
                        .roles("ADMIN")
                        .build(),
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("userpassword")
                        .roles("USER")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/data/load", "/api/reconciliation/**").hasRole("ADMIN")
                .antMatchers("/data/view", "/api/transactions/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .maximumSessions(1)  // Limits concurrent sessions to one per user
                .maxSessionsPreventsLogin(true)  // Prevents new logins if max sessions reached
                .expiredUrl("/login?expired=true")  // Redirect to login page if session expired
                .and()
                .sessionFixation().migrateSession()  // Prevents session fixation attacks
                .and()
                .logout()
                .logoutUrl("/logout")  // Defines custom logout URL
                .logoutSuccessUrl("/login?logout=true")  // Redirects to login after logout
                .deleteCookies("JSESSIONID")  // Removes session cookie on logout
                .invalidateHttpSession(true);  // Invalidates session on logout
        return http.build();
    }

}
