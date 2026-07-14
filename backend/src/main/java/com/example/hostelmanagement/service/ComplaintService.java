package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import java.util.List;


public interface ComplaintService {


    ApiResponse addComplaint(ComplaintRequest request);


    List<ComplaintResponse> getAllComplaints();


    ComplaintResponse getComplaintById(Long id);


    ApiResponse updateComplaint(Long id, ComplaintUpdateRequest request);


    ApiResponse deleteComplaint(Long id);


    List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status);


    List<ComplaintResponse> getComplaintsByCategory(ComplaintCategory category);


    List<ComplaintResponse> getComplaintsByTenant(Long tenantId);


    ComplaintStatisticsResponse getComplaintStatistics();
}
