package com.sporton.SportOn.repository;

import com.sporton.SportOn.entity.TimeSlot;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    @Query(value = "SELECT * FROM time_slot WHERE (start_time = :startTime1 OR end_time = :endTime1 OR (start_time BETWEEN :startTime2 AND :endTime2) OR (end_time BETWEEN :startTime2 AND :endTime2)) AND court_id = :courtId", nativeQuery = true)
    Optional<List<TimeSlot>> findByStartTimeOrEndTimeOrStartTimeBetweenAndEndTimeBetweenAndCourtId(
            LocalTime startTime1,
            LocalTime endTime1,
            LocalTime startTime2,
            LocalTime endTime2,
            Long courtId
    );

    Optional<List<TimeSlot>> findByCourtId(Long courtId, PageRequest pageRequest);
}
