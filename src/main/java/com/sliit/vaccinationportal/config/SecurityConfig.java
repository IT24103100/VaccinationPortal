package com.sliit.vaccinationportal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Specific role-based rules, ordered from most privileged to least.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // --- THIS IS THE CORRECTED RULE ---
                        // Allow both ADMIN and INVENTORY_MANAGER to access inventory pages.
                        .requestMatchers("/inventory/**").hasAnyRole("ADMIN", "INVENTORY_MANAGER")

                        .requestMatchers("/moh/**").hasRole("MINISTRY_OF_HEALTH")
                        .requestMatchers("/phi/**").hasRole("PHI")
                        .requestMatchers("/nurse/**").hasRole("NURSE")
                        .requestMatchers("/user/**").hasRole("USER")

                        .requestMatchers("/review/**", "/appointment/**").authenticated()

                        // Static resources that anyone can access.
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.*",
                                "/*.jpg",
                                "/*.jpeg",
                                "/*.png",
                                "/*.gif",
                                "/*.svg",
                                "/*.ico"
                        ).permitAll()

                        // Public URLs that anyone can access.
                        .requestMatchers(
                                "/",
                                "/register/**",
                                "/login",
                                "/forgot-password",
                                "/reset-password"
                        ).permitAll()

                        // Any other request must be authenticated.
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}