package com.library.management.controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import com.library.management.service.DashboardService;
import com.library.management.dto.DashboardStatsDTO;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin/dashboard") 
    public String dashboard(Model model) {
       
        DashboardStatsDTO stats = dashboardService.getOverviewStats();
        
        model.addAttribute("stats", stats);
        return "dashboard"; 
    }
}