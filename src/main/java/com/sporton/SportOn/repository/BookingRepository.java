package com.sporton.SportOn.repository;

import com.sporton.SportOn.dto.MonthlyIncome;
import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.entity.BookingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByUserId(Pageable pageable, Long userId);

    Optional<List<Booking>> findByProviderId(Pageable pageable, Long id);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = :status AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double findTotalIncomeByDateRange(@Param("status") BookingStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT YEAR(b.matchDate) AS year, MONTH(b.matchDate) AS month, SUM(b.totalPrice) AS totalIncome " +
            "FROM Booking b " +
            "WHERE b.matchDate >= :startDate " +
            "GROUP BY YEAR(b.matchDate), MONTH(b.matchDate) " +
            "ORDER BY YEAR(b.matchDate), MONTH(b.matchDate)")
    List<MonthlyIncome> findMonthlyIncome(@Param("startDate") LocalDate startDate);

    List<Booking> findByStatus(BookingStatus status);

    }