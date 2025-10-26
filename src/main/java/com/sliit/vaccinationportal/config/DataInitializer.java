package com.sliit.vaccinationportal.config;

import com.sliit.vaccinationportal.factory.UserFactory;
import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.repository.IT24103103.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            System.out.println("Database is empty. Initializing sample data...");

            // --- Create an Admin User using UserFactory ---
            if (userRepository.findByEmail("admin@hospital.com").isEmpty()) {
                User admin = userFactory.createAdmin("Admin User", "admin@hospital.com", "000000000V", "123");
                userRepository.save(admin);
            }

            // --- Create a Nurse User using UserFactory ---
            if (userRepository.findByEmail("nurse@hospital.com").isEmpty()) {
                User nurse = userFactory.createNurse("Nurse", "nurse@hospital.com", "111111111V", "123");
                userRepository.save(nurse);
            }

            // --- Create a normal Patient/User using UserFactory ---
            if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
                User patient = userFactory.createPatient("User Patien", "user@gmail.com", "222222222V", "123");
                userRepository.save(patient);
            }

            System.out.println("Sample data has been initialized.");
        }
    }
}