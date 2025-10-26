package com.sliit.vaccinationportal.controller.IT24103103;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.model.VaccinationRecord;
import com.sliit.vaccinationportal.repository.IT24103100.VaccinationRecordRepository;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaccinationRecordRepository recordRepository;

    @GetMapping("/user/profile")
    public String viewUserProfile(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<VaccinationRecord> records = recordRepository.findByUserIdOrderByVaccinationDateDesc(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("records", records);
        return "profile-view";
    }

    @GetMapping("/user/profile/edit")
    public String editUserProfileForm(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/user/profile/update")
    public String updateUserProfile(@ModelAttribute("user") User user, Principal principal, RedirectAttributes redirectAttributes) {
        if (!principal.getName().equals(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Security error: You can only edit your own profile.");
            return "redirect:/user/profile";
        }
        userService.updateUserProfile(user);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/user/profile";
    }

    @GetMapping("/nurse/profile")
    public String viewNurseProfile(Model model, Principal principal) {
        User nurse = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Nurse not found"));
        model.addAttribute("user", nurse);
        model.addAttribute("activePage", "profile");
        return "nurse-profile-view";
    }

    @GetMapping("/nurse/profile/edit")
    public String editNurseProfileForm(Model model, Principal principal) {
        User nurse = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Nurse not found"));
        model.addAttribute("user", nurse);
        model.addAttribute("activePage", "profile");
        return "nurse-profile-edit";
    }

    @PostMapping("/nurse/profile/update")
    public String updateNurseProfile(@ModelAttribute("user") User user, Principal principal, RedirectAttributes redirectAttributes) {
        if (!principal.getName().equals(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Security error: You can only edit your own profile.");
            return "redirect:/nurse/profile";
        }
        userService.updateUserProfile(user);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/nurse/profile";
    }
}