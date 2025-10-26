package com.sliit.vaccinationportal.controller.IT24103048;

import com.sliit.vaccinationportal.service.IT24103048.PdfReportService;
import com.sliit.vaccinationportal.service.IT24103048.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PdfReportService pdfReportService;

    @GetMapping
    public String reportsDashboard(Model model, Principal principal) {

        Map<String, Object> summary = reportService.generateDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("adminName", principal.getName());

        return "IT24103048/admin-reports-dashboard";
    }

    @GetMapping("/vaccinations")
    public String vaccinationReport(Model model,
                                    @RequestParam(name = "startDate", required = false) String startDateStr,
                                    @RequestParam(name = "endDate", required = false) String endDateStr) {

        LocalDate endDate = (endDateStr != null) ? LocalDate.parse(endDateStr) : LocalDate.now();
        LocalDate startDate = (startDateStr != null) ? LocalDate.parse(startDateStr) : endDate.minusDays(30);

        Map<String, Object> report = reportService.generateVaccinationReport(startDate, endDate);

        model.addAttribute("report", report);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "IT24103048/admin-vaccination-report";
    }

    @GetMapping("/users")
    public String userReport(Model model) {
        Map<String, Object> report = reportService.generateUserReport();
        model.addAttribute("report", report);

        return "IT24103048/admin-user-report";
    }

    @GetMapping("/inventory")
    public String inventoryReport(Model model) {
        Map<String, Object> report = reportService.generateInventoryReport();
        model.addAttribute("report", report);

        return "IT24103048/admin-inventory-report";
    }

    @GetMapping("/appointments")
    public String appointmentReport(Model model,
                                    @RequestParam(name = "startDate", required = false) String startDateStr,
                                    @RequestParam(name = "endDate", required = false) String endDateStr) {

        LocalDate endDate = (endDateStr != null) ? LocalDate.parse(endDateStr) : LocalDate.now();
        LocalDate startDate = (startDateStr != null) ? LocalDate.parse(startDateStr) : endDate.minusDays(30);

        Map<String, Object> report = reportService.generateAppointmentReport(startDate, endDate);

        model.addAttribute("report", report);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "IT24103048/admin-appointment-report";
    }

    @GetMapping("/download/comprehensive")
    public ResponseEntity<byte[]> downloadComprehensiveReport() {
        try {
            byte[] pdfBytes = pdfReportService.generateComprehensiveReport();

            String fileName = "VaxConnect_Comprehensive_Report_" + 
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}