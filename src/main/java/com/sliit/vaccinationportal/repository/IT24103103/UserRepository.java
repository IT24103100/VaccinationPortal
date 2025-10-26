package com.sliit.vaccinationportal.repository.IT24103103;

import com.sliit.vaccinationportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByResetPasswordToken(String token);

    Optional<User> findByNic(String nic);

    List<User> findAllByRole(String role);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> findUserCountByRole();

    @Query("SELECT CAST(u.createdDate AS date), COUNT(u) FROM User u WHERE u.createdDate >= ?1 GROUP BY CAST(u.createdDate AS date)")
    List<Object[]> findRecentRegistrations(LocalDateTime since);


    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate >= ?1")
    Long countRegistrationsSince(LocalDateTime date);
}