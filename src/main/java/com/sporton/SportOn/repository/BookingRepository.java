package com.sporton.SportOn.repository;

import com.sporton.SportOn.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByUserId(Long userId);

    Optional<List<Booking>> findByProviderId(Long id);
}
