package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;
import java.util.List;


public interface RentService {


    ApiResponse generateRent(GenerateRentRequest request);


    ApiResponse payRent(Long rentId, RentPaymentRequest request);


    List<RentResponse> getPendingRents();


    List<RentResponse> getRentHistory();


    List<RentResponse> getTenantRentHistory(Long tenantId);


    RentResponse getRentById(Long id);


    MonthlyRevenueResponse getMonthlyRevenue(String month);


    PendingAmountResponse getPendingAmount();
}
