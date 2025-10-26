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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inventory/admin")
public class AdminInventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String adminInventoryDashboard(Model model) {
        // Get current user information for admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> currentUser = userService.findUserByEmail(username);
        
        if (currentUser.isPresent()) {
            model.addAttribute("adminName", currentUser.get().getName());
            model.addAttribute("adminEmail", currentUser.get().getEmail());
        }
        
        List<VaccineStock> stocks = inventoryService.getAllStocks();
        model.addAttribute("stocks", stocks);
        model.addAttribute("newVaccine", new VaccineStock());
        return "IT24103067/admin-inventory-dashboard";
    }
}
