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

//    @Query(value = "SELECT * FROM time_slot WHERE (start_time = :startTime1 OR end_time = :endTime1 OR (start_time BETWEEN :startTime2 AND :endTime2) OR (end_time BETWEEN :startTime2 AND :endTime2)) AND court_id = :courtId", nativeQuery = true)
//    Optional<List<TimeSlot>> findByStartTimeOrEndTimeOrStartTimeBetweenAndEndTimeBetweenAndCourtId(
//            LocalTime startTime1,
//            LocalTime endTime1,
//            LocalTime startTime2,
//            LocalTime endTime2,
//            Long courtId
//    );

//    @Query(value = "SELECT * FROM time_slot WHERE (TO_CHAR(CAST(start_time AS TIME), 'HH12:MI AM') = :startTime1 OR TO_CHAR(CAST(end_time AS TIME), 'HH12:MI AM') = :endTime1 OR (TO_CHAR(CAST(start_time AS TIME), 'HH12:MI AM') BETWEEN :startTime2 AND :endTime2) OR (TO_CHAR(CAST(end_time AS TIME), 'HH12:MI AM') BETWEEN :startTime2 AND :endTime2)) AND court_id = :courtId", nativeQuery = true)
//    Optional<List<TimeSlot>> findByMatchingOrOverlappingTimeSlotsForCourt(
//            String startTime1,
//            String endTime1,
//            String startTime2,
//            String endTime2,
//            Long courtId
//    );

    @Query(value = "SELECT * FROM time_slot WHERE court_id = :courtId AND (" +
            "TO_TIMESTAMP(start_time, 'HH12:MI AM') < TO_TIMESTAMP(:endTime, 'HH12:MI AM') AND " +
            "TO_TIMESTAMP(end_time, 'HH12:MI AM') > TO_TIMESTAMP(:startTime, 'HH12:MI AM')" +
            ")", nativeQuery = true)
    Optional<List<TimeSlot>> findByMatchingOrOverlappingTimeSlotsForCourt(
            String startTime,
            String endTime,
            Long courtId
    );

    Optional<List<TimeSlot>> findByCourtId(Long courtId, PageRequest pageRequest);
}
