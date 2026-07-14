package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.TenantRequest;
import com.example.hostelmanagement.dto.TenantResponse;
import com.example.hostelmanagement.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> addTenant(@Valid @RequestBody TenantRequest request) {
        ApiResponse response = tenantService.addTenant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable("id") Long id) {
        TenantResponse tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTenant(
            @PathVariable("id") Long id,
            @Valid @RequestBody TenantRequest request
    ) {
        ApiResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTenant(@PathVariable("id") Long id) {
        ApiResponse response = tenantService.deleteTenant(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<TenantResponse>> getTenantsByRoom(@PathVariable("roomId") Long roomId) {
        List<TenantResponse> tenants = tenantService.getTenantsByRoom(roomId);
        return ResponseEntity.ok(tenants);
    }


    @GetMapping("/search")
    public ResponseEntity<TenantResponse> searchTenantByEmail(@RequestParam("email") String email) {
        TenantResponse tenant = tenantService.searchTenantByEmail(email);
        return ResponseEntity.ok(tenant);
    }


    @GetMapping("/active")
    public ResponseEntity<List<TenantResponse>> getActiveTenants() {
        List<TenantResponse> tenants = tenantService.getActiveTenants();
        return ResponseEntity.ok(tenants);
    }
}
