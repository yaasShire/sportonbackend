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

    List<BookedVenueResponseDTO> getBookingsByUserId(String phoneNumber , Integer page, Integer size) throws CommonException;

    CommonResponseModel updateBooking(Long bookingId, BookingRequest body) throws CommonException;

    CommonResponseModel cancelBooking(Long bookingId) throws CommonException;

    CommonResponseModel confirmtBooking(Long bookingId) throws CommonException;

    List<ProviderOrderResponseDTO> getBookingsByProviderId(String phoneNumber , Integer page, Integer size) throws CommonException;

    List<Booking> getBookingByCustomerId(String phoneNumber, Integer page, Integer size) throws CommonException;

    CommonResponseModel getNumberOfNewOrders(String phoneNumber, Integer page, Integer size) throws CommonException;

    CommonResponseModel getPendingOrders(String phoneNumber, Integer page, Integer size) throws CommonException;

    CommonResponseModel getConfirmedOrders(String phoneNumber , Integer page, Integer size) throws CommonException;

    CommonResponseModel getCancelledOrders(String phoneNumber , Integer page, Integer size) throws CommonException;

    CommonResponseModel getNumberOfTodayOrders(String phoneNumber , Integer page, Integer size);

    CommonResponseModel getNumberOfTodayMatches(String phoneNumber , Integer page, Integer size);

    CommonResponseModel getNumberOfPendingOrders(String phoneNumber , Integer page, Integer size);

    CommonResponseModel getMatchesByDate(String phoneNumber, MatchesRequestModel body , Integer page, Integer size) throws CommonException;

    CommonResponseModel getTotalIncomeByPeriod(String periodType);

    CommonResponseModel getLast12MonthsIncome(String phoneNumber) throws CommonException;
}
