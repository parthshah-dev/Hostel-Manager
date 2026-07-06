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

/**
 * REST controller for executing CRUD, filter, and search operations on Tenants.
 * Restricted to ADMIN role (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Endpoint to register a new tenant in the system.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addTenant(@Valid @RequestBody TenantRequest request) {
        ApiResponse response = tenantService.addTenant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to list all tenants.
     */
    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }

    /**
     * Endpoint to retrieve a tenant by database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable("id") Long id) {
        TenantResponse tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    /**
     * Endpoint to update details of an existing tenant profile.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTenant(
            @PathVariable("id") Long id,
            @Valid @RequestBody TenantRequest request
    ) {
        ApiResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to delete a tenant.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTenant(@PathVariable("id") Long id) {
        ApiResponse response = tenantService.deleteTenant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to list all tenants assigned to a specific room.
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<TenantResponse>> getTenantsByRoom(@PathVariable("roomId") Long roomId) {
        List<TenantResponse> tenants = tenantService.getTenantsByRoom(roomId);
        return ResponseEntity.ok(tenants);
    }

    /**
     * Endpoint to search a tenant profile by email.
     */
    @GetMapping("/search")
    public ResponseEntity<TenantResponse> searchTenantByEmail(@RequestParam("email") String email) {
        TenantResponse tenant = tenantService.searchTenantByEmail(email);
        return ResponseEntity.ok(tenant);
    }

    /**
     * Endpoint to retrieve active tenants only.
     */
    @GetMapping("/active")
    public ResponseEntity<List<TenantResponse>> getActiveTenants() {
        List<TenantResponse> tenants = tenantService.getActiveTenants();
        return ResponseEntity.ok(tenants);
    }
}
