package com.sliit.vaccinationportal.factory;

import com.sliit.vaccinationportal.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Factory Method Pattern for User Creation
 * 
 * This factory provides a clean, centralized way to create different types of users
 * with their specific configurations. It encapsulates the user creation logic
 * and makes the code more maintainable and testable.
 * 
 * Benefits:
 * - Single Responsibility: Each method handles one type of user creation
 * - Open/Closed Principle: Easy to add new user types without modifying existing code
 * - DRY Principle: Eliminates code duplication across different user creation points
 * - Consistency: Ensures all users of the same type are created with the same configuration
 */
@Component
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a standard patient/user with ROLE_USER
     * Used for patient registration and self-registration
     * 
     * @param name User's full name
     * @param email User's email address
     * @param nic User's NIC number
     * @param password Plain text password (will be encoded)
     * @param address User's address (optional)
     * @param postalCode User's postal code (optional)
     * @param birthday User's birthday (optional)
     * @param phoneNumber User's phone number (optional)
     * @return Configured User object ready for saving
     */
    public User createPatient(String name, String email, String nic, String password,
                             String address, String postalCode, java.time.LocalDate birthday, String phoneNumber) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setNic(nic);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        user.setAddress(address);
        user.setPostalCode(postalCode);
        user.setBirthday(birthday);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    /**
     * Creates a patient with minimal required fields
     * Used for quick patient creation
     * 
     * @param name User's full name
     * @param email User's email address
     * @param nic User's NIC number
     * @param password Plain text password (will be encoded)
     * @return Configured User object ready for saving
     */
    public User createPatient(String name, String email, String nic, String password) {
        return createPatient(name, email, nic, password, null, null, null, null);
    }

    /**
     * Creates an admin user with ROLE_ADMIN
     * Used for admin user creation and system initialization
     * 
     * @param name Admin's full name
     * @param email Admin's email address
     * @param nic Admin's NIC number
     * @param password Plain text password (will be encoded)
     * @return Configured User object ready for saving
     */
    public User createAdmin(String name, String email, String nic, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setNic(nic);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");
        return user;
    }

    /**
     * Creates a nurse user with ROLE_NURSE
     * Used for nurse user creation and system initialization
     * 
     * @param name Nurse's full name
     * @param email Nurse's email address
     * @param nic Nurse's NIC number
     * @param password Plain text password (will be encoded)
     * @return Configured User object ready for saving
     */
    public User createNurse(String name, String email, String nic, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setNic(nic);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_NURSE");
        return user;
    }

    /**
     * Creates a user with a custom role
     * Used for flexible user creation with specific roles
     * 
     * @param name User's full name
     * @param email User's email address
     * @param nic User's NIC number
     * @param password Plain text password (will be encoded)
     * @param role Custom role for the user
     * @return Configured User object ready for saving
     */
    public User createUserWithRole(String name, String email, String nic, String password, String role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setNic(nic);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    /**
     * Creates a user from an existing User object with proper encoding
     * Used when you have a User object but need to ensure proper password encoding
     * 
     * @param user Existing User object
     * @return User object with properly encoded password
     */
    public User createUserFromExisting(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            // Only encode if password is not already encoded
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return user;
    }
}
