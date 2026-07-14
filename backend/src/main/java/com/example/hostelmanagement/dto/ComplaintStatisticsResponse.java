package com.example.hostelmanagement.dto;


public record ComplaintStatisticsResponse(
    long totalComplaints,
    long open,
    long inProgress,
    long resolved
) {}
