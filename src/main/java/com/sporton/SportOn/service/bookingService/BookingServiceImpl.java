package com.sporton.SportOn.service.bookingService;

import com.sporton.SportOn.entity.*;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.bookingModel.*;
import com.sporton.SportOn.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            if (optionalTimeSlot.isEmpty()) {
                throw new CommonException("Time Slot With id " + body.getTimeSlotId() + " does not exist");
            }

            TimeSlot timeSlot = optionalTimeSlot.get();
            List<LocalDate> bookedDates = timeSlot.getBookedDates();

            if (!bookedDates.contains(body.getMatchDate())) {
                Optional<Court> optionalCourt = courtRepository.findById(body.getCourtId());
                if (optionalCourt.isEmpty()) throw new CommonException("Court With Id " + body.getCourtId() + " Does Not Exist");

                Optional<Venue> optionalVenue = venueRepository.findById(optionalCourt.get().getVenueId());
                if (optionalVenue.isEmpty()) throw new CommonException("Invalid Venue");

                // Save time slot by adding new booking date
                bookedDates.add(body.getMatchDate());
                timeSlot.setBookedDates(bookedDates);
                timeSlotRepository.save(timeSlot);

                Booking book = Booking.builder()
                        .courtId(body.getCourtId())
                        .timeSlotId(body.getTimeSlotId())
                        .matchDate(body.getMatchDate())
                        .bookingDate(LocalDate.now())
                        .totalPrice(body.getTotalPrice())
                        .userId(optionalAppUser.get().getId())
                        .providerId(optionalVenue.get().getProviderId())
                        .status(BookingStatus.Pending)
                        .venue(optionalVenue.get())
                        .build();
                bookingRepository.save(book);

                return CommonResponseModel.builder()
                        .status(HttpStatus.CREATED)
                        .message("Booking Placed Successfully")
                        .build();
            } else {
                throw new CommonException("This Time Slot, The Court Is Not Available");
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }


    @Override
    public List<BookedVenueResponseDTO> getBookingsByUserId(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            Optional<List<Booking>> optionalBookings = bookingRepository.findByUserId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for user");
            }

            List<Booking> bookings = optionalBookings.get();
            List<BookedVenueResponseDTO> bookedVenueResponseDTOs = bookings.stream().map(booking -> {
                Venue venue = booking.getVenue();
                Court court = null;
                try {
                    court = courtRepository.findById(booking.getCourtId())
                            .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                } catch (CommonException e) {
                    throw new RuntimeException(e);
                }
                TimeSlot timeSlot;
                try {
                     timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                            .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                } catch (CommonException e) {
                    throw new RuntimeException(e);
                }

                String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;
                return BookedVenueResponseDTO.builder()
                        .id(booking.getId())
                        .venueId(venue.getId())
                        .venueName(venue.getName())
                        .bookingDate(booking.getBookingDate())
                        .matchDate(booking.getMatchDate())
                        .courtName(court.getName())
                        .venuePhoneNumber(venue.getPhoneNumber())
                        .startTime(timeSlot.getStartTime())
                        .endTime(timeSlot.getEndTime())
                        .totalPrice(booking.getTotalPrice())
                        .status(BookingStatus.valueOf(booking.getStatus().name()))
                        .image(firstImage)
                        .build();
            }).collect(Collectors.toList());

            return bookedVenueResponseDTOs;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel updateBooking(Long bookingId, BookingRequest body) throws CommonException {
        try {
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
            if (optionalBooking.isPresent()){
                optionalBooking.get()
                        .setMatchDate(body.getMatchDate());
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
                if (optionalBooking.get().getStatus()==BookingStatus.Pending){
                    optionalBooking.get().setStatus(BookingStatus.Confirmed);
                    bookingRepository.save(optionalBooking.get());
                    return CommonResponseModel.builder()
                            .status(HttpStatus.OK)
                            .message("Booking Confirmed Successfully")
                            .build();
                }else {
                    throw new CommonException("The order is not a Pending order" + bookingId);
                }

            }else {
                throw new CommonException("There Is No Booking With Id " + bookingId);
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public List<ProviderOrderResponseDTO> getBookingsByProviderId(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for provider");
            }

            List<Booking> bookings = optionalBookings.get().stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.Pending)
                    .collect(Collectors.toList());

            if (bookings.isEmpty()) {
                throw new CommonException("No pending bookings found for provider");
            }

            List<ProviderOrderResponseDTO> providerOrderResponseDTOs = bookings.stream().map(booking -> {
                Venue venue = booking.getVenue();
                Court court = null;
                try {
                    court = courtRepository.findById(booking.getCourtId())
                            .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                } catch (CommonException e) {
                    throw new RuntimeException(e);
                }

                TimeSlot timeSlot = null;
                try {
                    timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                            .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                } catch (CommonException e) {
                    throw new RuntimeException(e);
                }

                String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;

                return ProviderOrderResponseDTO.builder()
                        .venueId(venue.getId())
                        .orderId(booking.getId())
                        .venueName(venue.getName())
                        .bookingDate(booking.getBookingDate())
                        .matchDate(booking.getMatchDate())
                        .courtName(court.getName())
                        .userPhoneNumber(optionalAppUser.get().getPhoneNumber())
                        .userName(optionalAppUser.get().getFullName())
                        .startTime(timeSlot.getStartTime())
                        .endTime(timeSlot.getEndTime())
                        .totalPrice(booking.getTotalPrice())
                        .status(BookingStatus.valueOf(booking.getStatus().name()))
                        .userProfileImage(optionalAppUser.get().getProfileImage())
                        .userProfileImage(firstImage)
                        .build();
            }).collect(Collectors.toList());

            return providerOrderResponseDTOs;
        } catch (Exception e) {
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

    public CommonResponseModel getNumberOfNewOrders(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for provider");
            }

            // Get today's date
            LocalDate today = LocalDate.now();

            // Filter bookings that are created today and are pending
            List<Booking> bookings = optionalBookings.get().stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.Pending)
                    .filter(booking -> booking.getBookingDate().isEqual(today))
                    .collect(Collectors.toList());

            int pendingOrdersCount = bookings.size();
            if (pendingOrdersCount == 0) {
                throw new CommonException("No pending bookings found for provider");
            }

            List<ProviderOrderResponseDTO> topPendingOrders = bookings.stream()
                    .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                    .limit(10)
                    .map(booking -> {
                        Venue venue = booking.getVenue();
                        Court court = null;
                        try {
                            court = courtRepository.findById(booking.getCourtId())
                                    .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        TimeSlot timeSlot = null;
                        try {
                            timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                                    .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;

                        Optional<AppUser> bookingUser = appUserRepository.findById(booking.getUserId());
                        if (bookingUser.isEmpty()) {
                            try {
                                throw new CommonException("User with id " + booking.getUserId() + " does not exist");
                            } catch (CommonException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        AppUser user = bookingUser.get();
                        return ProviderOrderResponseDTO.builder()
                                .venueId(venue.getId())
                                .orderId(booking.getId())
                                .venueName(venue.getName())
                                .bookingDate(booking.getBookingDate())
                                .matchDate(booking.getMatchDate())
                                .courtName(court.getName())
                                .userPhoneNumber(user.getPhoneNumber())
                                .userName(user.getFullName())
                                .startTime(timeSlot.getStartTime())
                                .endTime(timeSlot.getEndTime())
                                .totalPrice(booking.getTotalPrice())
                                .status(BookingStatus.valueOf(booking.getStatus().name()))
                                .userProfileImage(user.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            ProviderOrdersResponse response = new ProviderOrdersResponse(pendingOrdersCount, topPendingOrders);
            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Pending orders retrieved successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel getPendingOrders(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }
            Optional<List<Booking>> allBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            List<Booking> pendingBookings = new ArrayList<>();

            for (Booking booking : allBookings.get()) {
                if (booking.getStatus() == BookingStatus.Pending) {
                    pendingBookings.add(booking);
                }
            }

            if (pendingBookings.isEmpty()) {
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("No pending orders found")
                        .data(Collections.emptyList())
                        .build();
            }

            List<ProviderOrderResponseDTO> pendingOrders = pendingBookings.stream()
                    .map(booking -> {
                        Venue venue = booking.getVenue();
                        Court court = null;
                        try {
                            court = courtRepository.findById(booking.getCourtId())
                                    .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        TimeSlot timeSlot = null;
                        try {
                            timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                                    .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;

                        Optional<AppUser> bookingUser = appUserRepository.findById(booking.getUserId());
                        if (bookingUser.isEmpty()) {
                            try {
                                throw new CommonException("User with id " + booking.getUserId() + " does not exist");
                            } catch (CommonException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        AppUser user = bookingUser.get();

                        return ProviderOrderResponseDTO.builder()
                                .venueId(venue.getId())
                                .orderId(booking.getId())
                                .venueName(venue.getName())
                                .bookingDate(booking.getBookingDate())
                                .matchDate(booking.getMatchDate())
                                .courtName(court.getName())
                                .userPhoneNumber(user.getPhoneNumber())
                                .userName(user.getFullName())
                                .startTime(timeSlot.getStartTime())
                                .endTime(timeSlot.getEndTime())
                                .totalPrice(booking.getTotalPrice())
                                .status(booking.getStatus())
                                .userProfileImage(user.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Pending orders retrieved successfully")
                    .data(pendingOrders)
                    .build();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel getConfirmedOrders(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }
            Optional<List<Booking>> allBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            List<Booking> confirmedBookings = new ArrayList<>();

            for (Booking booking : allBookings.get()) {
                if (booking.getStatus() == BookingStatus.Confirmed) {
                    confirmedBookings.add(booking);
                }
            }

            if (confirmedBookings.isEmpty()) {
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("No pending orders found")
                        .data(Collections.emptyList())
                        .build();
            }

            List<ProviderOrderResponseDTO> pendingOrders = confirmedBookings.stream()
                    .map(booking -> {
                        Venue venue = booking.getVenue();
                        Court court = null;
                        try {
                            court = courtRepository.findById(booking.getCourtId())
                                    .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        TimeSlot timeSlot = null;
                        try {
                            timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                                    .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;

                        Optional<AppUser> bookingUser = appUserRepository.findById(booking.getUserId());
                        if (bookingUser.isEmpty()) {
                            try {
                                throw new CommonException("User with id " + booking.getUserId() + " does not exist");
                            } catch (CommonException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        AppUser user = bookingUser.get();
                        return ProviderOrderResponseDTO.builder()
                                .venueId(venue.getId())
                                .orderId(booking.getId())
                                .venueName(venue.getName())
                                .bookingDate(booking.getBookingDate())
                                .matchDate(booking.getMatchDate())
                                .courtName(court.getName())
                                .userPhoneNumber(user.getPhoneNumber())
                                .userName(user.getFullName())
                                .startTime(timeSlot.getStartTime())
                                .endTime(timeSlot.getEndTime())
                                .totalPrice(booking.getTotalPrice())
                                .status(booking.getStatus())
                                .userProfileImage(user.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Pending orders retrieved successfully")
                    .data(pendingOrders)
                    .build();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel getCancelledOrders(String phoneNumber) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }
            Optional<List<Booking>> allBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            List<Booking> confirmedBookings = new ArrayList<>();

            for (Booking booking : allBookings.get()) {
                if (booking.getStatus() == BookingStatus.Canceled) {
                    confirmedBookings.add(booking);
                }
            }

            if (confirmedBookings.isEmpty()) {
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("No pending orders found")
                        .data(Collections.emptyList())
                        .build();
            }

            List<ProviderOrderResponseDTO> pendingOrders = confirmedBookings.stream()
                    .map(booking -> {
                        Venue venue = booking.getVenue();
                        Court court = null;
                        try {
                            court = courtRepository.findById(booking.getCourtId())
                                    .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        TimeSlot timeSlot = null;
                        try {
                            timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                                    .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }

                        String firstImage = venue.getImages() != null && !venue.getImages().isEmpty() ? venue.getImages().get(0) : null;

                        Optional<AppUser> bookingUser = appUserRepository.findById(booking.getUserId());
                        if (bookingUser.isEmpty()) {
                            try {
                                throw new CommonException("User with id " + booking.getUserId() + " does not exist");
                            } catch (CommonException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        AppUser user = bookingUser.get();

                        return ProviderOrderResponseDTO.builder()
                                .venueId(venue.getId())
                                .orderId(booking.getId())
                                .venueName(venue.getName())
                                .bookingDate(booking.getBookingDate())
                                .matchDate(booking.getMatchDate())
                                .courtName(court.getName())
                                .userPhoneNumber(optionalAppUser.get().getPhoneNumber())
                                .userName(user.getFullName())
                                .startTime(timeSlot.getStartTime())
                                .endTime(timeSlot.getEndTime())
                                .totalPrice(booking.getTotalPrice())
                                .status(booking.getStatus())
                                .userProfileImage(user.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Pending orders retrieved successfully")
                    .data(pendingOrders)
                    .build();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel getNumberOfTodayOrders(String phoneNumber) {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }
            Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for provider");
            }

            // Get today's date
            LocalDate today = LocalDate.now();

            // Filter bookings that are created today
            long todayOrdersCount = optionalBookings.get().stream()
                    .filter(booking -> booking.getBookingDate().isEqual(today))
                    .count();

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Number of orders retrieved successfully")
                    .data(todayOrdersCount)
                    .build();
        } catch (Exception e) {
            return CommonResponseModel.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public CommonResponseModel getNumberOfTodayMatches(String phoneNumber) {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for provider");
            }

            // Get today's date
            LocalDate today = LocalDate.now();

            // Filter bookings that have a match scheduled for today and matchDate is not null
            long todayMatchesCount = optionalBookings.get().stream()
                    .filter(booking -> (booking.getMatchDate() != null && booking.getMatchDate().isEqual(today) )&& booking.getStatus()==BookingStatus.Confirmed)
                    .count();

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Number of matches scheduled for today retrieved successfully")
                    .data(todayMatchesCount)
                    .build();
        } catch (Exception e) {
            return CommonResponseModel.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public CommonResponseModel getNumberOfPendingOrders(String phoneNumber) {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            Optional<List<Booking>> optionalBookings = bookingRepository.findByProviderId(optionalAppUser.get().getId());
            if (optionalBookings.isEmpty() || optionalBookings.get().isEmpty()) {
                throw new CommonException("No bookings found for provider");
            }

            // Get today's date
            LocalDate today = LocalDate.now();

            // Filter bookings that have status of pending
            long pendingOrdersCount = optionalBookings.get().stream()
                    .filter(booking -> booking.getStatus()==BookingStatus.Pending)
                    .count();

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Number of pending orders retrieved successfully")
                    .data(pendingOrdersCount)
                    .build();
        } catch (Exception e) {
            return CommonResponseModel.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public CommonResponseModel getMatchesByDate(String phoneNumber, MatchesRequestModel body) throws CommonException {
        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByPhoneNumber(phoneNumber);
            if (optionalAppUser.isEmpty()) {
                throw new CommonException("User with phone number " + phoneNumber + " does not exist");
            }

            AppUser appUser = optionalAppUser.get();
            LocalDate targetDate = convertToLocalDate(body.getDate());

            List<Booking> confirmedBookings = bookingRepository.findByProviderId(appUser.getId())
                    .orElseThrow(() -> new CommonException("No bookings found for user"))
                    .stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.Confirmed)
                    .filter(booking -> booking.getMatchDate().equals(targetDate))
                    .collect(Collectors.toList());

            if (confirmedBookings.isEmpty()) {
                return CommonResponseModel.builder()
                        .status(HttpStatus.OK)
                        .message("No matches found for the specified date")
                        .data(Collections.emptyList())
                        .build();
            }

            List<ProviderOrderResponseDTO> matches = confirmedBookings.stream()
                    .map(booking -> {
                        Venue venue = booking.getVenue();
                        Court court = null;
                        try {
                            court = courtRepository.findById(booking.getCourtId())
                                    .orElseThrow(() -> new CommonException("Court with id " + booking.getCourtId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }
                        TimeSlot timeSlot = null;
                        try {
                            timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                                    .orElseThrow(() -> new CommonException("Time slot with id " + booking.getTimeSlotId() + " does not exist"));
                        } catch (CommonException e) {
                            throw new RuntimeException(e);
                        }
                        Optional<AppUser> bookingUser = appUserRepository.findById(booking.getUserId());
                        if (bookingUser.isEmpty()) {
                            try {
                                throw new CommonException("User with id " + booking.getUserId() + " does not exist");
                            } catch (CommonException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        AppUser user = bookingUser.get();

                        String firstImage = (venue.getImages() != null && !venue.getImages().isEmpty()) ? venue.getImages().get(0) : null;

                        return ProviderOrderResponseDTO.builder()
                                .venueId(venue.getId())
                                .orderId(booking.getId())
                                .venueName(venue.getName())
                                .bookingDate(booking.getBookingDate())
                                .matchDate(booking.getMatchDate())
                                .courtName(court.getName())
                                .userPhoneNumber(user.getPhoneNumber())
                                .userName(user.getFullName())
                                .startTime(timeSlot.getStartTime())
                                .endTime(timeSlot.getEndTime())
                                .totalPrice(booking.getTotalPrice())
                                .status(booking.getStatus())
                                .userProfileImage(user.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Matches retrieved successfully")
                    .data(matches)
                    .build();

        } catch (Exception e) {
            throw new CommonException("Error retrieving matches: " + e.getMessage());
        }
    }


    public LocalDate convertToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    public Double getTotalIncomeByPeriod(String periodType) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (periodType.toLowerCase()) {
            case "daily":
                startDate = endDate;
                break;
            case "weekly":
                startDate = endDate.minusDays(6);
                break;
            case "monthly":
                startDate = endDate.minusMonths(1).plusDays(1);
                break;
            case "yearly":
                startDate = endDate.minusYears(1).plusDays(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid period type. Use 'DAILY', 'WEEKLY', 'MONTHLY', or 'YEARLY'.");
        }

        return bookingRepository.findTotalIncomeByDateRange(BookingStatus.Confirmed, startDate, endDate);
    }

}
