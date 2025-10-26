package com.sliit.vaccinationportal.controller.IT24103048;

import com.sliit.vaccinationportal.model.Hospital;
import com.sliit.vaccinationportal.repository.IT24103048.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/hospitals")
public class HospitalController {

    @Autowired
    private HospitalRepository hospitalRepository;

    @GetMapping
    public String manageHospitals(Model model) {
        List<Hospital> hospitals = hospitalRepository.findAll();
        model.addAttribute("hospitals", hospitals);
        return "IT24103048/admin-manage-hospitals";
    }

    @GetMapping("/add")
    public String showAddHospitalForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        return "IT24103048/admin-add-hospital";
    }

    @PostMapping("/save")
    public String saveHospital(@ModelAttribute("hospital") Hospital hospital,
                               RedirectAttributes redirectAttributes) {

        // Validation 1: Check if hospital name is empty
        if (hospital.getName() == null || hospital.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hospital name is required!");
            return "redirect:/admin/hospitals/add";
        }

        // Validation 2: Check if district is empty
        if (hospital.getDistrict() == null || hospital.getDistrict().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "District is required!");
            return "redirect:/admin/hospitals/add";
        }

        // Validation 3: Check if city is empty
        if (hospital.getCity() == null || hospital.getCity().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "City is required!");
            return "redirect:/admin/hospitals/add";
        }

        // Validation 4: Check for duplicate hospital name in the same city
        Optional<Hospital> existingByNameAndCity = hospitalRepository.findByNameAndCity(hospital.getName(), hospital.getCity());
        if (existingByNameAndCity.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "A hospital with name '" + hospital.getName() + "' already exists in city '" + hospital.getCity() + "'!");
            return "redirect:/admin/hospitals/add";
        }

        // Validation 5: Validate contact number format if provided
        if (hospital.getContactNumber() != null && !hospital.getContactNumber().trim().isEmpty()) {
            // Sri Lankan phone format: XXX-XXXXXXX or XXXXXXXXXX
            if (!hospital.getContactNumber().matches("\\d{3}-\\d{7}") &&
                    !hospital.getContactNumber().matches("\\d{10}")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Invalid contact number format! Use 10 digits or XXX-XXXXXXX format.");
                return "redirect:/admin/hospitals/add";
            }
        }

        // Save hospital if all validations pass
        hospitalRepository.save(hospital);
        redirectAttributes.addFlashAttribute("successMessage", "Hospital added successfully!");
        return "redirect:/admin/hospitals";
    }

    @GetMapping("/edit/{id}")
    public String showEditHospitalForm(@PathVariable("id") Long hospitalId, Model model) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid hospital Id:" + hospitalId));
        model.addAttribute("hospital", hospital);
        return "IT24103048/admin-edit-hospital";
    }

    @PostMapping("/update")
    public String updateHospital(@ModelAttribute("hospital") Hospital hospital,
                                 RedirectAttributes redirectAttributes) {

        // Validation 1: Check if hospital exists
        if (!hospitalRepository.existsById(hospital.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hospital not found!");
            return "redirect:/admin/hospitals";
        }

        // Validation 2: Check if name is empty
        if (hospital.getName() == null || hospital.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hospital name is required!");
            return "redirect:/admin/hospitals/edit/" + hospital.getId();
        }

        // Validation 3: Check if district is empty
        if (hospital.getDistrict() == null || hospital.getDistrict().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "District is required!");
            return "redirect:/admin/hospitals/edit/" + hospital.getId();
        }

        // Validation 4: Check if city is empty
        if (hospital.getCity() == null || hospital.getCity().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "City is required!");
            return "redirect:/admin/hospitals/edit/" + hospital.getId();
        }

        // Validation 5: Check if another hospital has the same name in the same city
        Optional<Hospital> existingByNameAndCity = hospitalRepository.findByNameAndCity(hospital.getName(), hospital.getCity());
        if (existingByNameAndCity.isPresent() && !existingByNameAndCity.get().getId().equals(hospital.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Another hospital with name '" + hospital.getName() + "' already exists in city '" + hospital.getCity() + "'!");
            return "redirect:/admin/hospitals/edit/" + hospital.getId();
        }

        // Validation 6: Validate contact number format if provided
        if (hospital.getContactNumber() != null && !hospital.getContactNumber().trim().isEmpty()) {
            if (!hospital.getContactNumber().matches("\\d{3}-\\d{7}") &&
                    !hospital.getContactNumber().matches("\\d{10}")) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Invalid contact number format! Use 10 digits or XXX-XXXXXXX format.");
                return "redirect:/admin/hospitals/edit/" + hospital.getId();
            }
        }

        // Update hospital if all validations pass
        hospitalRepository.save(hospital);
        redirectAttributes.addFlashAttribute("successMessage", "Hospital updated successfully!");
        return "redirect:/admin/hospitals";
    }

    @GetMapping("/delete/{id}")
    public String deleteHospital(@PathVariable("id") Long hospitalId,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Validation: Check if hospital exists
            if (!hospitalRepository.existsById(hospitalId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Hospital not found!");
                return "redirect:/admin/hospitals";
            }

            // Delete the hospital
            hospitalRepository.deleteById(hospitalId);
            redirectAttributes.addFlashAttribute("successMessage", "Hospital deleted successfully!");

        } catch (Exception e) {
            // Handle any exceptions during deletion
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete hospital. It may be linked to existing appointments. Error: " + e.getMessage());
        }
        return "redirect:/admin/hospitals";
    }
}