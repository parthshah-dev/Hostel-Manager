package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.DashboardSummaryResponse;
import com.example.hostelmanagement.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving Hostel/PG management dashboard statistics.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint to fetch the dashboard summary metrics.
     * Accessible by ADMIN users only (enforced via SecurityConfig).
     *
     * @return DashboardSummaryResponse wrapped in ResponseEntity.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
