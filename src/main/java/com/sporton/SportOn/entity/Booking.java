package com.sporton.SportOn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
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
    private LocalDate bookingDate = LocalDate.now();
    private LocalDate matchDate = LocalDate.now();
    @NonNull
    private Double totalPrice;
    @NonNull
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id")
    private Venue venue;
}