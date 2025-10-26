package com.sliit.vaccinationportal.service.IT24103048;

import com.sliit.vaccinationportal.repository.IT24103031.AppointmentRepository;
import com.sliit.vaccinationportal.repository.IT24103103.UserRepository;
import com.sliit.vaccinationportal.repository.IT24103100.VaccinationRecordRepository;
import com.sliit.vaccinationportal.repository.IT24103067.VaccineStockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final UserRepository userRepository;
    private final VaccineStockRepository vaccineStockRepository;
    private final AppointmentRepository appointmentRepository;

    public ReportService(VaccinationRecordRepository vaccinationRecordRepository,
                         UserRepository userRepository,
                         VaccineStockRepository vaccineStockRepository,
                         AppointmentRepository appointmentRepository) {
        this.vaccinationRecordRepository = vaccinationRecordRepository;
        this.userRepository = userRepository;
        this.vaccineStockRepository = vaccineStockRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Generate vaccination statistics report
     */
    public Map<String, Object> generateVaccinationReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Object[]> vaccinationsByType = vaccinationRecordRepository
                .findVaccinationCountByVaccineType(startDate, endDate);

        List<Object[]> dailyVaccinations = vaccinationRecordRepository
                .findDailyVaccinationCounts(startDate, endDate);

        report.put("vaccineTypeCounts", vaccinationsByType);
        report.put("dailyCounts", dailyVaccinations);

        return report;
    }

    /**
     * Generate user statistics report
     */
    public Map<String, Object> generateUserReport() {
        Map<String, Object> report = new HashMap<>();

        List<Object[]> usersByRole = userRepository.findUserCountByRole();

        List<Object[]> recentRegistrations = userRepository
                .findRecentRegistrations(LocalDate.now().minusDays(30).atStartOfDay());

        report.put("userRoleData", usersByRole);
        report.put("recentRegistrations", recentRegistrations);

        return report;
    }

    /**
     * Generate inventory report
     */
    public Map<String, Object> generateInventoryReport() {
        Map<String, Object> report = new HashMap<>();

        List<Object[]> lowStockVaccines = vaccineStockRepository.findLowStockVaccines();

        List<Object[]> stockByType = vaccineStockRepository.findStockByVaccineType();

        report.put("lowStockVaccines", lowStockVaccines);
        report.put("fullStock", stockByType);

        return report;
    }

    /**
     * Generate appointment statistics report
     */
    public Map<String, Object> generateAppointmentReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Object[]> appointmentsByStatus = appointmentRepository
                .findAppointmentCountByStatus(startDate, endDate);

        List<Object[]> popularHospitals = appointmentRepository
                .findTopHospitalsByAppointments(startDate, endDate);

        report.put("appointmentStatusCounts", appointmentsByStatus);
        report.put("topHospitals", popularHospitals);

        return report;
    }

    /**
     * Generate dashboard summary statistics
     */
    public Map<String, Object> generateDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalUsers", userRepository.count());
        summary.put("totalVaccinations", vaccinationRecordRepository.count());
        summary.put("totalVaccineTypes", vaccineStockRepository.count());
        summary.put("totalAppointments", appointmentRepository.count());

        LocalDate lastWeekDate = LocalDate.now().minusDays(7);
        LocalDateTime lastWeekDateTime = LocalDateTime.now().minusDays(7);

        summary.put("recentVaccinations", vaccinationRecordRepository.countVaccinationsSince(lastWeekDate));
        summary.put("recentRegistrations", userRepository.countRegistrationsSince(lastWeekDateTime));

        return summary;
    }
}