package com.sporton.SportOn.model.bookingModel;

import com.sporton.SportOn.entity.BookingStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long courtId;
    private Long providerId;
    private Long timeSlotId;
    private LocalDate bookingDate;
    private Double totalPrice;
    private BookingStatus status;
}
