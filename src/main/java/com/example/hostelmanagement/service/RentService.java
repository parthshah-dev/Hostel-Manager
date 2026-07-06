package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;
import java.util.List;

/**
 * Service interface defining business processes for Rent management.
 */
public interface RentService {

    /**
     * Generates rent ledger records for all active tenants for the requested month.
     * Skips duplicate records.
     *
     * @param request DTO containing the target month (YYYY-MM).
     * @return ApiResponse.
     */
    ApiResponse generateRent(GenerateRentRequest request);

    /**
     * Records a rent payment for the specific ledger ID.
     *
     * @param rentId  ID of the rent record.
     * @param request DTO containing payment details.
     * @return ApiResponse.
     */
    ApiResponse payRent(Long rentId, RentPaymentRequest request);

    /**
     * Lists all pending/unpaid rent records.
     *
     * @return List of RentResponse.
     */
    List<RentResponse> getPendingRents();

    /**
     * Retrieves the complete rent history, sorted newest month first.
     *
     * @return List of RentResponse.
     */
    List<RentResponse> getRentHistory();

    /**
     * Retrieves the payment history for a specific tenant.
     *
     * @param tenantId Tenant ID.
     * @return List of RentResponse.
     */
    List<RentResponse> getTenantRentHistory(Long tenantId);

    /**
     * Retrieves a single rent record details by ID.
     *
     * @param id Rent ID.
     * @return RentResponse.
     */
    RentResponse getRentById(Long id);

    /**
     * Calculates the total collected revenue for the specified month (YYYY-MM).
     *
     * @param month Month query in YYYY-MM format.
     * @return MonthlyRevenueResponse DTO.
     */
    MonthlyRevenueResponse getMonthlyRevenue(String month);

    /**
     * Calculates the total outstanding due amount of all pending rent records.
     *
     * @return PendingAmountResponse DTO.
     */
    PendingAmountResponse getPendingAmount();
}
