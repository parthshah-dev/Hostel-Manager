package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.PaymentStatus;
import com.example.hostelmanagement.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Rent} entities.
 */
@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

    /**
     * Checks if a rent record already exists for the given tenant and month.
     *
     * @param tenantId  The tenant ID.
     * @param rentMonth The month in YYYY-MM format.
     * @return true if it exists, false otherwise.
     */
    boolean existsByTenantIdAndRentMonth(Long tenantId, String rentMonth);

    /**
     * Finds all rent records for the specified tenant.
     *
     * @param tenantId The tenant ID.
     * @return A list of Rent records.
     */
    List<Rent> findByTenantId(Long tenantId);

    /**
     * Finds all rent records with the specified payment status.
     *
     * @param paymentStatus The payment status.
     * @return A list of Rent records.
     */
    List<Rent> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Finds all rent records generated for the specified month.
     *
     * @param rentMonth The month in YYYY-MM format.
     * @return A list of Rent records.
     */
    List<Rent> findByRentMonth(String rentMonth);

    /**
     * Finds a specific rent record by tenant ID and month.
     *
     * @param tenantId  The tenant ID.
     * @param rentMonth The month in YYYY-MM format.
     * @return An Optional containing the Rent record, or empty if not found.
     */
    Optional<Rent> findByTenantIdAndRentMonth(Long tenantId, String rentMonth);

    /**
     * Finds all rent records with the specified payment status, sorted by month descending.
     *
     * @param paymentStatus The payment status.
     * @return A list of Rent records.
     */
    List<Rent> findByPaymentStatusOrderByRentMonthDesc(PaymentStatus paymentStatus);

    /**
     * Finds all rent records, sorted by month descending.
     *
     * @return A list of Rent records.
     */
    List<Rent> findAllByOrderByRentMonthDesc();

    /**
     * Calculates the total due amount of all rent records with the specified payment status.
     *
     * @param status The payment status.
     * @return The sum of due amounts, or 0 if none.
     */
    @Query("SELECT COALESCE(SUM(r.dueAmount), 0) FROM Rent r WHERE r.paymentStatus = :status")
    BigDecimal sumByPaymentStatus(@Param("status") PaymentStatus status);

    /**
     * Calculates the total amount paid during the current month (where payment status is PAID and month matches).
     *
     * @param rentMonth The month in YYYY-MM format.
     * @return The sum of paid amounts, or 0 if none.
     */
    @Query("SELECT COALESCE(SUM(r.amountPaid), 0) FROM Rent r WHERE r.paymentStatus = com.example.hostelmanagement.entity.PaymentStatus.PAID AND r.rentMonth = :rentMonth")
    BigDecimal sumPaidAmountForCurrentMonth(@Param("rentMonth") String rentMonth);
}
