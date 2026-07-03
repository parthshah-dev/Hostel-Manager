package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repository interface for managing {@link RentPayment} entities.
 */
@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    /**
     * Calculates the sum of all unpaid rent.
     *
     * @return The sum of unpaid rent, or 0 if none.
     */
    @Query("SELECT COALESCE(SUM(rp.amount), 0) FROM RentPayment rp WHERE rp.paid = false")
    BigDecimal sumUnpaidRent();

    /**
     * Calculates the sum of received payments during the specified date range.
     *
     * @param start Start of the range.
     * @param end   End of the range.
     * @return The sum of payments, or 0 if none.
     */
    @Query("SELECT COALESCE(SUM(rp.amount), 0) FROM RentPayment rp WHERE rp.paid = true AND rp.paymentDate >= :start AND rp.paymentDate <= :end")
    BigDecimal sumRevenueByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
