package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.PaymentStatus;
import com.example.hostelmanagement.entity.Rent;
import com.example.hostelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {


    boolean existsByTenantIdAndRentMonth(Long tenantId, String rentMonth);


    List<Rent> findByTenantId(Long tenantId);


    List<Rent> findByPaymentStatus(PaymentStatus paymentStatus);


    List<Rent> findByRentMonth(String rentMonth);


    Optional<Rent> findByTenantIdAndRentMonth(Long tenantId, String rentMonth);

    List<Rent> findByPaymentStatusAndRoomAdminOrderByRentMonthDesc(PaymentStatus paymentStatus, User admin);

    List<Rent> findByRoomAdminOrderByRentMonthDesc(User admin);

    @Query("SELECT COALESCE(SUM(r.dueAmount), 0) FROM Rent r WHERE r.paymentStatus = :status AND r.room.admin = :admin")
    BigDecimal sumByPaymentStatusAndAdmin(@Param("status") PaymentStatus status, @Param("admin") User admin);

    @Query("SELECT COALESCE(SUM(r.amountPaid), 0) FROM Rent r WHERE r.paymentStatus = com.example.hostelmanagement.entity.PaymentStatus.PAID AND r.rentMonth = :rentMonth AND r.room.admin = :admin")
    BigDecimal sumPaidAmountForCurrentMonthAndAdmin(@Param("rentMonth") String rentMonth, @Param("admin") User admin);
}
