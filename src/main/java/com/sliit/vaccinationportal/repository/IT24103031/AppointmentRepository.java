package com.sliit.vaccinationportal.repository.IT24103031;

import com.sliit.vaccinationportal.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN ?1 AND ?2")
    Long countAppointmentsByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN ?1 AND ?2 GROUP BY a.status")
    List<Object[]> findAppointmentCountByStatus(LocalDate startDate, LocalDate endDate);

    @Query("SELECT h.name, COUNT(a) FROM Appointment a JOIN a.hospital h WHERE a.appointmentDate BETWEEN ?1 AND ?2 GROUP BY h.name ORDER BY COUNT(a) DESC")
    List<Object[]> findTopHospitalsByAppointments(LocalDate startDate, LocalDate endDate);

    void deleteByUserId(Long userId);
}