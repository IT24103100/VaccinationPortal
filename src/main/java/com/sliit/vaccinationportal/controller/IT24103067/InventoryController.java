package com.sliit.vaccinationportal.controller.IT24103067;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.model.VaccineStock;
import com.sliit.vaccinationportal.service.IT24103067.InventoryService;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String inventoryDashboard(Model model) {
        // Check if user is admin or inventory manager
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> currentUser = userService.findUserByEmail(username);
        
        if (currentUser.isPresent()) {
            String role = currentUser.get().getRole();
            
            // If admin, redirect to admin inventory dashboard
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/inventory/admin/dashboard";
            }
            // If inventory manager, show inventory manager dashboard
            else if ("ROLE_INVENTORY_MANAGER".equals(role)) {
                model.addAttribute("managerName", currentUser.get().getName());
                model.addAttribute("managerEmail", currentUser.get().getEmail());
            }
        }
        
        List<VaccineStock> stocks = inventoryService.getAllStocks();
        model.addAttribute("stocks", stocks);
        model.addAttribute("newVaccine", new VaccineStock());
        return "IT24103067/inventory-dashboard";
    }

    @GetMapping("/vaccine/add")
    public String showAddVaccineForm(Model model) {
        // Ensure the form has a bound object so fields render correctly
        model.addAttribute("newVaccine", new VaccineStock());
        return "IT24103067/inventory-add-vaccine";
    }

    @PostMapping("/vaccine/add")
    public String addNewVaccine(@ModelAttribute("newVaccine") VaccineStock vaccine, RedirectAttributes redirectAttributes) {
        if (inventoryService.findByVaccineName(vaccine.getVaccineName()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vaccine with this name already exists.");
        } else {
            inventoryService.addNewVaccine(vaccine);
            redirectAttributes.addFlashAttribute("successMessage", "New vaccine type added successfully.");
        }
        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/stock/update")
    public String updateStockQuantity(@RequestParam("stockId") Long stockId,
                                      @RequestParam("quantity") int quantity,
                                      RedirectAttributes redirectAttributes) {
        inventoryService.updateStockQuantity(stockId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully.");
        return "redirect:/inventory/dashboard";
    }

    @GetMapping("/vaccine/edit/{id}")
    public String showEditVaccineForm(@PathVariable("id") Long stockId, Model model) {
        VaccineStock stock = inventoryService.findStockById(stockId);
        model.addAttribute("vaccineToEdit", stock);
        return "IT24103067/inventory-edit-vaccine";
    }

    @PostMapping("/vaccine/update")
    public String updateStock(@ModelAttribute("vaccineToEdit") VaccineStock stock, RedirectAttributes redirectAttributes) {
        // We now call the new, more general update service method.
        inventoryService.updateStock(stock);
        redirectAttributes.addFlashAttribute("successMessage", "Vaccine stock details updated successfully.");
        return "redirect:/inventory/dashboard";
    }

    @GetMapping("/vaccine/delete/{id}")
    public String deleteVaccine(@PathVariable("id") Long stockId, RedirectAttributes redirectAttributes) {
        inventoryService.deleteVaccine(stockId);
        redirectAttributes.addFlashAttribute("successMessage", "Vaccine type deleted successfully.");
        return "redirect:/inventory/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> currentUser = userService.findUserByEmail(username);
        
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        } else {
            return "redirect:/inventory/dashboard?error=user_not_found";
        }
        
        return "IT24103067/profile-view";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> currentUser = userService.findUserByEmail(username);
        
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        } else {
            return "redirect:/inventory/dashboard?error=user_not_found";
        }
        
        return "IT24103067/profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveExistingUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile. Please try again.");
        }
        return "redirect:/inventory/profile";
    }
}