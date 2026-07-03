package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.DashboardSummaryResponse;

/**
 * Service interface defining operations for generating Dashboard summary metrics.
 */
public interface DashboardService {

    /**
     * Retrives the aggregated Hostel/PG dashboard summary.
     *
     * @return DashboardSummaryResponse DTO.
     */
    DashboardSummaryResponse getDashboardSummary();
}
