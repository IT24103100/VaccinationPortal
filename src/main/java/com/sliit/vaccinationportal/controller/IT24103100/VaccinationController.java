package com.sliit.vaccinationportal.controller.IT24103100;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.model.VaccinationRecord;
import com.sliit.vaccinationportal.model.VaccineStock;
import com.sliit.vaccinationportal.service.IT24103067.InventoryService;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import com.sliit.vaccinationportal.service.IT24103100.VaccinationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Handles all nurse-facing vaccination record operations.
 * Responsibilities:
 * - Search/select patients and start add-record flows
 * - Render forms for adding and editing records
 * - Persist new records and updates, with validation and flash messages
 * - Load vaccination history and patient details views
 */
@Controller
@RequestMapping("/nurse")
public class VaccinationController {

    private final UserService userService;
    private final InventoryService inventoryService;
    private final VaccinationService vaccinationService;

    public VaccinationController(UserService userService,
                                 InventoryService inventoryService,
                                 VaccinationService vaccinationService) {
        // Constructor injection for required services
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.vaccinationService = vaccinationService;
    }

    // ============= Add Record Operations =============

    /**
     * Shows the initial add-record page with a patient search.
     * If a NIC is provided, pre-populates the search results.
     */
    @GetMapping("/add-record")
    public String showAddRecordPage(Model model,
                                    Principal principal,
                                    @RequestParam(name = "nic", required = false) String nic) {
        prepareForm(model, principal);
        searchAndAddPatientToModel(nic, model);
        model.addAttribute("activePage", "add-record");
        return "IT24103100/add-record-form";
    }

    /**
     * Shows the full add-record form for a specific patient.
     */
    @GetMapping("/add-record/{patientId}")
    public String showAddRecordFullPage(@PathVariable Long patientId, Model model, Principal principal) {
        User patient = userService.findUserById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID: " + patientId));

        prepareForm(model, principal);
        model.addAttribute("patient", patient);
        model.addAttribute("activePage", "add-record");
        return "IT24103100/add-record-full-page";
    }

    /**
     * Persists a new vaccination record for a patient.
     * Performs date validation and protects against duplicate doses.
     */
    @PostMapping("/save-record")
    public String saveVaccinationRecord(@Valid @ModelAttribute("vaccinationRecord") VaccinationRecord record,
                                        BindingResult bindingResult,
                                        @RequestParam("userId") Long userId,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        User patient = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID: " + userId));

        if (record.getVaccinationDate() != null && record.getVaccinationDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("vaccinationDate", "futureDate", "Vaccination date cannot be in the future.");
        }

        // populate audit createdBy from logged-in nurse
        String nurseEmail = ((org.springframework.security.core.userdetails.User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        record.setCreatedBy(nurseEmail);

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("vaccines", inventoryService.getAllStocks());
            return "IT24103100/add-record-full-page";
        }

        if (isDuplicateDose(userId, record, redirectAttributes)) {
            return "redirect:/nurse/add-record?nic=" + patient.getNic();
        }

        try {
            vaccinationService.createVaccinationRecord(userId, record);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Record added and stock updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to save record. Reason: " + e.getMessage());
            System.err.println("Error creating vaccination record: " + e.getMessage());
        }
        return "redirect:/nurse/patient/" + userId;
    }

    // ============= View Patient Details Operations =============

    /**
     * Renders the patient details page with their vaccination history.
     */
    @GetMapping("/patient/{id}")
    public String viewPatientDetails(@PathVariable("id") Long patientId, Model model, Principal principal) {
        User patient = userService.findUserById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID: " + patientId));
        List<VaccinationRecord> records = vaccinationService.getHistoryForUser(patientId);

        model.addAttribute("patient", patient);
        model.addAttribute("records", records);
        model.addAttribute("activePage", "");
        return "IT24103100/patient-details";
    }

    // ============= Edit History Operations =============

    /**
     * Shows the page to search patients and edit their history.
     */
    @GetMapping("/edit-history")
    public String showEditHistoryPage(@RequestParam(name = "nic", required = false) String nic,
                                      Model model, Principal principal) {
        searchAndAddPatientToModel(nic, model);
        model.addAttribute("activePage", "edit-history");
        return "IT24103100/nurse-edit-history";
    }

    /**
     * Returns the history table fragment for async inclusion on the page.
     */
    @GetMapping("/patient-history/{id}")
    public String getPatientHistoryFragment(@PathVariable("id") Long patientId, Model model) {
        List<VaccinationRecord> history = vaccinationService.getHistoryForUser(patientId);
        model.addAttribute("patientHistory", history);
        return "IT24103100/_history-table-fragment :: historyTable";
    }

    /**
     * Shows the edit form for a specific vaccination record.
     */
    @GetMapping("/record/edit/{id}")
    public String showEditRecordForm(@PathVariable("id") Long recordId, Model model, Principal principal) {
        VaccinationRecord record = vaccinationService.findRecordById(recordId);
        List<VaccineStock> vaccines = inventoryService.getAllStocks();

        model.addAttribute("recordToEdit", record);
        model.addAttribute("vaccines", vaccines);
        model.addAttribute("activePage", "edit-history");
        return "IT24103100/edit-record-form";
    }

    /**
     * Updates an existing vaccination record.
     * Validates required edit reason, date, and duplicate dose constraints.
     */
    @PostMapping("/record/update")
    public String updateRecord(@Valid @ModelAttribute("recordToEdit") VaccinationRecord record,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               @RequestParam(value = "editReason", required = false) String editReason) {
        if (editReason == null || editReason.trim().isEmpty()) {
            bindingResult.reject("editReason", "Reason for edit is required.");
        }

        String nurseEmail = ((org.springframework.security.core.userdetails.User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        record.setUpdatedBy(nurseEmail);
        if (record.getVaccinationDate() != null && record.getVaccinationDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("vaccinationDate", "futureDate", "Vaccination date cannot be in the future.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("vaccines", inventoryService.getAllStocks());
            return "IT24103100/edit-record-form";
        }

        if (isDuplicateDoseForUpdate(record, redirectAttributes)) {
            return "redirect:/nurse/edit-history?nic=" + record.getUser().getNic();
        }

        VaccinationRecord updatedRecord = vaccinationService.updateVaccinationRecord(record);
        redirectAttributes.addFlashAttribute("successMessage", "Record updated successfully!");
        return "redirect:/nurse/patient/" + updatedRecord.getUser().getId();
    }

    /**
     * Deletes a vaccination record and restores stock for that vaccine.
     */
    @PostMapping("/record/delete/{id}")
    public String deleteRecord(@PathVariable("id") Long recordId, RedirectAttributes redirectAttributes) {
        try {
            VaccinationRecord record = vaccinationService.findRecordById(recordId);
            Long userId = record.getUser().getId();

            vaccinationService.deleteVaccinationRecord(recordId);
            redirectAttributes.addFlashAttribute("successMessage", "Record deleted successfully!");
            return "redirect:/nurse/patient/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting record: " + e.getMessage());
            return "redirect:/nurse/edit-history";
        }
    }

    // ============= Helper Methods =============

    /**
     * Prepares a blank form model with nurse info and available vaccines.
     */
    private void prepareForm(Model model, Principal principal) {
        String nurseEmail = principal.getName();
        User loggedInNurse = userService.findUserByEmail(nurseEmail)
                .orElseThrow(() -> new IllegalStateException("Nurse not found"));

        VaccinationRecord record = new VaccinationRecord();
        record.setAdministeredBy(loggedInNurse.getName());

        model.addAttribute("vaccinationRecord", record);
        model.addAttribute("vaccines", inventoryService.getAllStocks());
    }

    /**
     * Adds patient search results to the model based on NIC, or lists all patients.
     */
    private void searchAndAddPatientToModel(String nic, Model model) {
        if (nic != null && !nic.trim().isEmpty()) {
            userService.findUserByNic(nic).ifPresentOrElse(
                    foundPatient -> model.addAttribute("patientList", List.of(foundPatient)),
                    () -> {
                        model.addAttribute("searchError", "Patient not found for NIC: " + nic);
                        model.addAttribute("patientList", Collections.emptyList());
                    }
            );
        } else {
            model.addAttribute("patientList", userService.findAllPatients());
        }
    }

    /**
     * Returns true and sets an error message if the same dose already exists for this user.
     */
    private boolean isDuplicateDose(Long userId, VaccinationRecord record,
                                    RedirectAttributes redirectAttributes) {
        if (vaccinationService.isDoseAlreadyAdministered(userId,
                record.getVaccineName(), record.getDose())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This dose has already been administered to the patient.");
            return true;
        }
        return false;
    }

    /**
     * Returns true and sets an error message if the same dose exists on another record for the user.
     */
    private boolean isDuplicateDoseForUpdate(VaccinationRecord record,
                                             RedirectAttributes redirectAttributes) {
        if (vaccinationService.isDoseAlreadyAdministeredForAnotherRecord(
                record.getUser().getId(), record.getVaccineName(),
                record.getDose(), record.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This dose has already been administered to the patient.");
            return true;
        }
        return false;
    }
}