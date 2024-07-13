package com.sporton.SportOn.controller;

import com.sporton.SportOn.configuration.JWTService;
import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.BookedVenueResponseDTO;
import com.sporton.SportOn.model.bookingModel.BookingRequest;
import com.sporton.SportOn.model.bookingModel.MatchesRequestModel;
import com.sporton.SportOn.model.bookingModel.ProviderOrderResponseDTO;
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
    public List<BookedVenueResponseDTO> getBookingsByUserId(
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
    public List<ProviderOrderResponseDTO> getBookingsByProviderId(
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

//        @GetMapping("/getBookingByCustomerId")
//    public List<Booking> getBookingByCustomerId(
//            @RequestHeader("Authorization") String authorizationHeader
//    ) throws CommonException {
//        try {
//            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
//            String phoneNumber = jwtService.extractUsername(token);
//            return bookingService.getBookingByCustomerId(phoneNumber);
//        }catch (Exception e){
//            throw new CommonException(e.getMessage());
//        }
//    }

    @GetMapping("/getBookingByCustomerId")
    public List<BookedVenueResponseDTO> getBookingByCustomerId(
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

    @GetMapping("/getTop10NewOrders")
    public CommonResponseModel getNumberOfNewOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getNumberOfNewOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getPendingOrders")
    public CommonResponseModel getPendingOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getPendingOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getConfirmedOrders")
    public CommonResponseModel getConfirmedOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getConfirmedOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getCancelledOrders")
    public CommonResponseModel getCancelledOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getCancelledOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getNumberOfTodayOrders")
    public CommonResponseModel getNumberOfTodayOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getNumberOfTodayOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getNumberOfTodayMatches")
    public CommonResponseModel getNumberOfTodayMatches(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getNumberOfTodayMatches(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/getNumberOfPendingOrders")
    public CommonResponseModel getNumberOfPendingOrders(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getNumberOfPendingOrders(phoneNumber);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @PostMapping("/getMatchesByDate")
    public CommonResponseModel getMatchesByDate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody MatchesRequestModel body
            ) throws CommonException {
        try {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            String phoneNumber = jwtService.extractUsername(token);
            return bookingService.getMatchesByDate(phoneNumber, body);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @GetMapping("/income")
    public Double getTotalIncomeByPeriod(@RequestParam String periodType) throws CommonException {
        try {
            return bookingService.getTotalIncomeByPeriod(periodType);
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }
}
