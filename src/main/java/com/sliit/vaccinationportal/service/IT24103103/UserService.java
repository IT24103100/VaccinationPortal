package com.sliit.vaccinationportal.service.IT24103103;

import com.sliit.vaccinationportal.factory.UserFactory;
import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.repository.IT24103103.UserRepository;
import com.sliit.vaccinationportal.repository.IT24103031.AppointmentRepository;
import com.sliit.vaccinationportal.repository.IT24103036.ReviewRepository;
import com.sliit.vaccinationportal.repository.IT24103100.VaccinationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final ReviewRepository reviewRepository;
    private final UserFactory userFactory;

    // Use constructor injection for all dependencies - this is a best practice.
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AppointmentRepository appointmentRepository,
                       VaccinationRecordRepository vaccinationRecordRepository,
                       ReviewRepository reviewRepository,
                       UserFactory userFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepository = appointmentRepository;
        this.vaccinationRecordRepository = vaccinationRecordRepository;
        this.reviewRepository = reviewRepository;
        this.userFactory = userFactory;
    }

    public void saveUser(User user) {
        // Use UserFactory to create a properly configured patient user
        User configuredUser = userFactory.createUserFromExisting(user);
        if (configuredUser.getRole() == null) {
            configuredUser.setRole("ROLE_USER");
        }
        userRepository.save(configuredUser);
    }

    public void createUserByAdmin(User user) {
        // Use UserFactory to create a properly configured user with admin-specified role
        User configuredUser = userFactory.createUserFromExisting(user);
        userRepository.save(configuredUser);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findAllPatients() {
        return userRepository.findAllByRole("ROLE_USER");
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllUsers();
        }
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByNic(String nic) {
        return userRepository.findByNic(nic);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void saveExistingUser(User user) {
        userRepository.save(user);
    }

    /**
     * Deletes a user and all their related data (appointments, records, reviews)
     * to prevent foreign key constraint violations. This is a transactional operation.
     * @param id The ID of the user to be deleted.
     */
    @Transactional
    public void deleteUserById(Long id) {
        // Step 1: Delete all dependent records from child tables first.
        appointmentRepository.deleteByUserId(id);
        vaccinationRecordRepository.deleteByUserId(id);
        reviewRepository.deleteByUserId(id);

        // Step 2: After all dependencies are removed, delete the user from the parent table.
        userRepository.deleteById(id);
    }

    public User updateUserProfile(User updatedData) {
        User existingUser = userRepository.findByEmail(updatedData.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setName(updatedData.getName());
        existingUser.setAddress(updatedData.getAddress());
        existingUser.setPostalCode(updatedData.getPostalCode());
        existingUser.setPhoneNumber(updatedData.getPhoneNumber());
        existingUser.setBirthday(updatedData.getBirthday());
        return userRepository.save(existingUser);
    }

    // --- Password Reset Methods ---

    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }
}