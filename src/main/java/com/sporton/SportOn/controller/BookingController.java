package com.sporton.SportOn.controller;

import com.sporton.SportOn.configuration.JWTService;
import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.BookingRequest;
import com.sporton.SportOn.service.bookingService.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final JWTService jwtService;
    @PostMapping("/book")
    public CommonResponseModel bookCourt(
            @RequestBody BookingRequest body,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.bookCourt(body, phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/get")
    public List<Booking> getBookingsByUserId(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getBookingsByUserId(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getBookingByProviderId")
    public List<Booking> getBookingsByProviderId(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getBookingsByProviderId(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

        @GetMapping("/getBookingByCustomerId")
    public List<Booking> getBookingByCustomerId(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getBookingByCustomerId(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @PutMapping("/update/{bookingId}")
    public CommonResponseModel updateBooking(@PathVariable Long bookingId, @RequestBody BookingRequest body) throws CommonException {
        try {
            return bookingService.updateBooking(bookingId, body);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @PutMapping("/cancel/{bookingId}")
    public CommonResponseModel cancelBooking(@PathVariable Long bookingId) throws CommonException {
        try {
            return bookingService.cancelBooking(bookingId);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @PutMapping("/accept/{bookingId}")
    public CommonResponseModel acceptBooking(@PathVariable Long bookingId) throws CommonException {
        try {
            return bookingService.confirmtBooking(bookingId);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

}
