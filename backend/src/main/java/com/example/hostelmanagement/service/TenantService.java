package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.TenantRequest;
import com.example.hostelmanagement.dto.TenantResponse;
import java.util.List;


public interface TenantService {


    ApiResponse addTenant(TenantRequest request);


    List<TenantResponse> getAllTenants();


    TenantResponse getTenantById(Long id);


    ApiResponse updateTenant(Long id, TenantRequest request);


    ApiResponse deleteTenant(Long id);


    List<TenantResponse> getTenantsByRoom(Long roomId);


    TenantResponse searchTenantByEmail(String email);


    List<TenantResponse> getActiveTenants();
}
