package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.service.RentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST controller executing CRUD and aggregation operations on Rent records.
 * Restricted to ADMIN role (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/api/rents")
public class RentController {

    private final RentService rentService;

    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    /**
     * Endpoint to generate monthly rent records for all active tenants.
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse> generateRent(@Valid @RequestBody GenerateRentRequest request) {
        ApiResponse response = rentService.generateRent(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to record rent payments.
     */
    @PutMapping("/{rentId}/pay")
    public ResponseEntity<ApiResponse> payRent(
            @PathVariable("rentId") Long rentId,
            @Valid @RequestBody RentPaymentRequest request
    ) {
        ApiResponse response = rentService.payRent(rentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to list all pending/unpaid rent records.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<RentResponse>> getPendingRents() {
        List<RentResponse> pending = rentService.getPendingRents();
        return ResponseEntity.ok(pending);
    }

    /**
     * Endpoint to retrieve complete rent history (sorted newest month first).
     */
    @GetMapping("/history")
    public ResponseEntity<List<RentResponse>> getRentHistory() {
        List<RentResponse> history = rentService.getRentHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Endpoint to fetch rent payment history for a specific tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentResponse>> getTenantRentHistory(@PathVariable("tenantId") Long tenantId) {
        List<RentResponse> history = rentService.getTenantRentHistory(tenantId);
        return ResponseEntity.ok(history);
    }

    /**
     * Endpoint to fetch details of a specific rent record.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentResponse> getRentById(@PathVariable("id") Long id) {
        RentResponse rent = rentService.getRentById(id);
        return ResponseEntity.ok(rent);
    }

    /**
     * Endpoint to calculate total collections for a specific month (defaults to current calendar month).
     */
    @GetMapping("/monthly-revenue")
    public ResponseEntity<MonthlyRevenueResponse> getMonthlyRevenue(
            @RequestParam(value = "month", required = false) String month
    ) {
        if (month == null || month.isBlank()) {
            month = DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        }
        MonthlyRevenueResponse response = rentService.getMonthlyRevenue(month);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to calculate total outstanding due amounts across all pending rents.
     */
    @GetMapping("/pending-amount")
    public ResponseEntity<PendingAmountResponse> getPendingAmount() {
        PendingAmountResponse response = rentService.getPendingAmount();
        return ResponseEntity.ok(response);
    }
}
