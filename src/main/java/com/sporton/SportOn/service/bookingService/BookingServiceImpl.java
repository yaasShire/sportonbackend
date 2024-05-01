package com.sporton.SportOn.service.bookingService;

import com.sporton.SportOn.entity.*;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.BookingRequest;
import com.sporton.SportOn.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final AppUserRepository appUserRepository;
    private  final VenueRepository venueRepository;
    private final CourtRepository courtRepository;
    @Override
    public CommonResponseModel bookCourt(BookingRequest body, String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) throw new CommonException("User Is Not Found");
            Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(body.getTimeSlotId());
            if (optionalTimeSlot.isPresent()){
                if (optionalTimeSlot.get().getAvailable()){
                    Optional<Court> optionalCourt = courtRepository.findById(body.getCourtId());
                    if (optionalCourt.isEmpty()) throw new CommonException("Court With Id " + body.getCourtId() + " Does Not Exist");
                    Optional<Venue> optionalVenue = venueRepository.findById(optionalCourt.get().getVenueId());
                    if (optionalVenue.isEmpty()) throw new CommonException("Invalid Venue");
                    optionalTimeSlot.get().setBookedDates(Collections.singletonList(body.getBookingDate()));
                    Booking book = Booking.builder()
                            .courtId(body.getCourtId())
                            .timeSlotId(body.getTimeSlotId())
                            .bookingDate(body.getBookingDate())
                            .totalPrice(body.getTotalPrice())
                            .userId(optionalAppUser.get().getId())
                            .providerId(optionalVenue.get().getProviderId())
                            .status(BookingStatus.Pending)
                            .build();
                    log.info("Provider id {}", optionalVenue.get().getProviderId());
                    bookingRepository.save(book);
                    optionalTimeSlot.get().setAvailable(false);
                    timeSlotRepository.save(optionalTimeSlot.get());
                    return CommonResponseModel.builder()
                            .status(HttpStatus.CREATED)
                            .message("Booking Placed Successfully")
                            .build();
                }else {
                    throw new CommonException("This Time Slot, The Court Is Not Available");
                }
            }else {
                throw new CommonException("Time Slot With id " + body.getTimeSlotId() + " does not exit");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByUserId( String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> appUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (appUser.isPresent()){
               Optional<List<Booking>> optionalBookings = bookingRepository.findByUserId(appUser.get().getId());
               if (optionalBookings.isPresent()){
                   return optionalBookings.get();
               }else {
                   throw new CommonException("Noo Bookings Found");
               }
            }else {
                throw new CommonException("User With Id " + appUser.get().getId() +" Does Not Exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }
    @Override
    public CommonResponseModel updateBooking(Long bookingId, BookingRequest body) throws CommonException {
        try {
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
            if (optionalBooking.isPresent()){
                optionalBooking.get()
                        .setBookingDate(body.getBookingDate());
                Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(body.getTimeSlotId());
                if (optionalTimeSlot.isPresent()){
                    if (optionalTimeSlot.get().getAvailable()){
                        optionalBooking.get().setTimeSlotId(body.getTimeSlotId());
                    }else {
                        throw new CommonException("This Time Slot, The Court Is Not Available");
                    }
                }else {
                    throw new CommonException("Time Slot With id " + body.getTimeSlotId() + " does not exit");
                }
                bookingRepository.save(optionalBooking.get());
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("Booking Updated Successfully")
                        .build();
            }else {
                throw new CommonException("There Is No Booking With Id " + bookingId);
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel cancelBooking(Long bookingId) throws CommonException {
        try {
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
            if (optionalBooking.isPresent()){
//                cancel logic happens here
                optionalBooking.get().setStatus(BookingStatus.Canceled);
                bookingRepository.save(optionalBooking.get());
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("Booking Canceled Successfully")
                        .build();
            }else {
                throw new CommonException("There Is No Booking With Id " + bookingId);
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel confirmtBooking(Long bookingId) throws CommonException {
        try {
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
            if (optionalBooking.isPresent()){
//                cancel logic happens here
                optionalBooking.get().setStatus(BookingStatus.Confirmed);
                bookingRepository.save(optionalBooking.get());
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("Booking Confirmed Successfully")
                        .build();
            }else {
                throw new CommonException("There Is No Booking With Id " + bookingId);
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByProviderId(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> appUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (appUser.isPresent()){
                Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(appUser.get().getId());

                if (optionalBookings.isPresent()){
                    return optionalBookings.get();
                }else {
                    throw new CommonException("Noo Bookings Found");
                }
            }else {
                throw new CommonException("User With Id " + appUser.get().getId() +" Does Not Exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingByCustomerId(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> appUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (appUser.isPresent()){
                Optional<List<Booking>> optionalBookings = bookingRepository.findByUserId(appUser.get().getId());
                if (optionalBookings.isPresent()){
                    return optionalBookings.get();
                }else {
                    throw new CommonException("Noo Bookings Found");
                }
            }else {
                throw new CommonException("User With Id " + appUser.get().getId() +" Does Not Exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }
}
