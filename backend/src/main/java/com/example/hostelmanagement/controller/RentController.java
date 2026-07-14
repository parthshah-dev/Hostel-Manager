package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.service.RentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/api/rents")
public class RentController {

    private final RentService rentService;

    public RentController(RentService rentService) {
        this.rentService = rentService;
    }


    @PostMapping("/generate")
    public ResponseEntity<ApiResponse> generateRent(@Valid @RequestBody GenerateRentRequest request) {
        ApiResponse response = rentService.generateRent(request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{rentId}/pay")
    public ResponseEntity<ApiResponse> payRent(
            @PathVariable("rentId") Long rentId,
            @Valid @RequestBody RentPaymentRequest request
    ) {
        ApiResponse response = rentService.payRent(rentId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/pending")
    public ResponseEntity<List<RentResponse>> getPendingRents() {
        List<RentResponse> pending = rentService.getPendingRents();
        return ResponseEntity.ok(pending);
    }


    @GetMapping("/history")
    public ResponseEntity<List<RentResponse>> getRentHistory() {
        List<RentResponse> history = rentService.getRentHistory();
        return ResponseEntity.ok(history);
    }


    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentResponse>> getTenantRentHistory(@PathVariable("tenantId") Long tenantId) {
        List<RentResponse> history = rentService.getTenantRentHistory(tenantId);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RentResponse> getRentById(@PathVariable("id") Long id) {
        RentResponse rent = rentService.getRentById(id);
        return ResponseEntity.ok(rent);
    }


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


    @GetMapping("/pending-amount")
    public ResponseEntity<PendingAmountResponse> getPendingAmount() {
        PendingAmountResponse response = rentService.getPendingAmount();
        return ResponseEntity.ok(response);
    }
}
