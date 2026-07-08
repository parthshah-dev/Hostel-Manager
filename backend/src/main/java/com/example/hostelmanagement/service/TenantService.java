package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.TenantRequest;
import com.example.hostelmanagement.dto.TenantResponse;
import java.util.List;

/**
 * Service interface defining business processes for Tenant management.
 */
public interface TenantService {

    /**
     * Adds a new Tenant and updates Room occupancy/status.
     *
     * @param request DTO carrying tenant creation inputs.
     * @return ApiResponse confirming successful creation.
     */
    ApiResponse addTenant(TenantRequest request);

    /**
     * Retrieves all tenants.
     *
     * @return List of TenantResponse.
     */
    List<TenantResponse> getAllTenants();

    /**
     * Retrieves a tenant by database ID.
     *
     * @param id Database ID of the tenant.
     * @return TenantResponse.
     */
    TenantResponse getTenantById(Long id);

    /**
     * Updates an existing tenant profile and re-evaluates Room capacities if room assignment changes.
     *
     * @param id      The database ID.
     * @param request DTO carrying updated attributes.
     * @return ApiResponse.
     */
    ApiResponse updateTenant(Long id, TenantRequest request);

    /**
     * Deletes a tenant from the database and updates Room status based on remaining tenants.
     *
     * @param id Database ID of the tenant.
     * @return ApiResponse.
     */
    ApiResponse deleteTenant(Long id);

    /**
     * Finds all tenants assigned to a specific room.
     *
     * @param roomId Database ID of the room.
     * @return List of TenantResponse.
     */
    List<TenantResponse> getTenantsByRoom(Long roomId);

    /**
     * Searches for a tenant by email.
     *
     * @param email Email query.
     * @return TenantResponse.
     */
    TenantResponse searchTenantByEmail(String email);

    /**
     * Retrieves all active tenants.
     *
     * @return List of TenantResponse.
     */
    List<TenantResponse> getActiveTenants();
}
