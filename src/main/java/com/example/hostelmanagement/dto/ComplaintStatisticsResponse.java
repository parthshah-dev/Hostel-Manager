package com.example.hostelmanagement.dto;

/**
 * DTO record representing complaint workflow statistics metrics.
 */
public record ComplaintStatisticsResponse(
    long totalComplaints,
    long open,
    long inProgress,
    long resolved
) {}
