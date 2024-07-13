package com.sporton.SportOn.repository;

import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.entity.BookingStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByUserId(Long userId);

    Optional<List<Booking>> findByProviderId(Long id);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = :status AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double findTotalIncomeByDateRange(@Param("status") BookingStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}