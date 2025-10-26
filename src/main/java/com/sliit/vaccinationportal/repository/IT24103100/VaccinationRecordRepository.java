package com.sliit.vaccinationportal.repository.IT24103100;

import com.sliit.vaccinationportal.model.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for vaccination records.
 * Exposes query methods used by services and reports.
 */
@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {

    /** Returns a patient's vaccination history ordered by date (newest first). */
    List<VaccinationRecord> findByUserIdOrderByVaccinationDateDesc(Long userId);

    /** Aggregates vaccination counts per day in a date range. */
    @Query("SELECT COUNT(v), v.vaccinationDate FROM VaccinationRecord v WHERE v.vaccinationDate BETWEEN ?1 AND ?2 GROUP BY v.vaccinationDate")
    List<Object[]> findVaccinationCountByDateRange(LocalDate startDate, LocalDate endDate);

    /** Aggregates vaccination counts by vaccine type in a date range. */
    @Query("SELECT v.vaccineName, COUNT(v) FROM VaccinationRecord v WHERE v.vaccinationDate BETWEEN ?1 AND ?2 GROUP BY v.vaccineName ORDER BY COUNT(v) DESC")
    List<Object[]> findVaccinationCountByVaccineType(LocalDate startDate, LocalDate endDate);

    /** Daily counts ordered by date for charting. */
    @Query("SELECT v.vaccinationDate, COUNT(v) FROM VaccinationRecord v WHERE v.vaccinationDate BETWEEN ?1 AND ?2 GROUP BY v.vaccinationDate ORDER BY v.vaccinationDate")
    List<Object[]> findDailyVaccinationCounts(LocalDate startDate, LocalDate endDate);

    /** Total count since a given date. */
    @Query("SELECT COUNT(v) FROM VaccinationRecord v WHERE v.vaccinationDate >= ?1")
    Long countVaccinationsSince(LocalDate date);

    /** Deletes all records for a user (used for cascading account cleanup). */
    void deleteByUserId(Long userId);

    /** Checks if a user has already received a particular dose of a vaccine. */
    boolean existsByUserIdAndVaccineNameAndDose(Long userId, String vaccineName, String dose);

    /** Same as above but excluding a specific record ID (for updates). */
    boolean existsByUserIdAndVaccineNameAndDoseAndIdNot(Long userId, String vaccineName, String dose, Long recordId);
}
