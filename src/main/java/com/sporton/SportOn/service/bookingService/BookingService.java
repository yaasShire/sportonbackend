package com.sporton.SportOn.service.bookingService;

import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.BookedVenueResponseDTO;
import com.sporton.SportOn.model.bookingModel.BookingRequest;
import com.sporton.SportOn.model.bookingModel.MatchesRequestModel;
import com.sporton.SportOn.model.bookingModel.ProviderOrderResponseDTO;

import java.util.List;

public interface BookingService {
    CommonResponseModel bookCourt(BookingRequest body, String phoneNumber) throws CommonException;

    List<BookedVenueResponseDTO> getBookingsByUserId(String phoneNumber) throws CommonException;

    CommonResponseModel updateBooking(Long bookingId, BookingRequest body) throws CommonException;

    CommonResponseModel cancelBooking(Long bookingId) throws CommonException;

    CommonResponseModel confirmtBooking(Long bookingId) throws CommonException;

    List<ProviderOrderResponseDTO> getBookingsByProviderId(String phoneNumber) throws CommonException;

    List<Booking> getBookingByCustomerId(String phoneNumber) throws CommonException;

    CommonResponseModel getNumberOfNewOrders(String phoneNumber) throws CommonException;

    CommonResponseModel getPendingOrders(String phoneNumber) throws CommonException;

    CommonResponseModel getConfirmedOrders(String phoneNumber) throws CommonException;

    CommonResponseModel getCancelledOrders(String phoneNumber) throws CommonException;

    CommonResponseModel getNumberOfTodayOrders(String phoneNumber);

    CommonResponseModel getNumberOfTodayMatches(String phoneNumber);

    CommonResponseModel getNumberOfPendingOrders(String phoneNumber);

    CommonResponseModel getMatchesByDate(String phoneNumber, MatchesRequestModel body) throws CommonException;

    Double getTotalIncomeByPeriod(String periodType);
}
