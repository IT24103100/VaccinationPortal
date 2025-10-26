package com.sliit.vaccinationportal.service.IT24103103;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.repository.IT24103103.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * This service class implements Spring Security's UserDetailsService interface.
 * It is responsible for loading a user's details (including username, password, and roles)
 * from the database during the authentication process.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * This method is called by Spring Security when a user attempts to log in.
     * It finds the user by their email (which we use as the username) and
     * converts the user entity into a Spring Security UserDetails object.
     *
     * @param email The email address entered by the user in the login form.
     * @return A UserDetails object containing the user's credentials and authorities.
     * @throws UsernameNotFoundException if a user with the given email is not found,
     *         or if the found user does not have a role assigned.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new UsernameNotFoundException("User does not have a role assigned: " + email);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}