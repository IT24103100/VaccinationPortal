package com.sliit.vaccinationportal.repository.IT24103048;

import com.sliit.vaccinationportal.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // Find hospital by name for duplicate checking
    Optional<Hospital> findByName(String name);

    // Optional: Check if hospital exists with specific name, district and city combination
    boolean existsByNameAndDistrictAndCity(String name, String district, String city);

    // Find hospital by name and city for duplicate checking
    Optional<Hospital> findByNameAndCity(String name, String city);
}