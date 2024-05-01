package com.sporton.SportOn.service.bookingService;

import com.sporton.SportOn.entity.Booking;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.BookingRequest;

import java.util.List;

public interface BookingService {
    CommonResponseModel bookCourt(BookingRequest body, String phoneNumber) throws CommonException;

    List<Booking> getBookingsByUserId(String phoneNumber) throws CommonException;

    CommonResponseModel updateBooking(Long bookingId, BookingRequest body) throws CommonException;

    CommonResponseModel cancelBooking(Long bookingId) throws CommonException;

    CommonResponseModel confirmtBooking(Long bookingId) throws CommonException;

    List<Booking> getBookingsByProviderId(String phoneNumber) throws CommonException;

    List<Booking> getBookingByCustomerId(String phoneNumber) throws CommonException;
}
