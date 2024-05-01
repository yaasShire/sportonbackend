package com.sporton.SportOn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long userId;
    @NonNull
    private Long providerId;
    @NonNull
    private Long courtId;
    @NonNull
    private Long timeSlotId;
    @NonNull
    private LocalDate bookingDate;
    @NonNull
    private Double totalPrice;
    @NonNull
    private BookingStatus status;
}