package com.sporton.SportOn.service.timeSlotService;

import com.sporton.SportOn.entity.Court;
import com.sporton.SportOn.entity.TimeSlot;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.timeSlotModel.TimeSlotRequest;
import com.sporton.SportOn.repository.CourtRepository;
import com.sporton.SportOn.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotServiceImpl implements TimeSlotService{
    private final TimeSlotRepository timeSlotRepository;
    private final CourtRepository courtRepository;
    @Override
    public CommonResponseModel createTimeSlot(TimeSlotRequest body) throws CommonException {
        try {
            Optional<Court> optionalCourt = courtRepository.findById(body.getCourtId());
            if (optionalCourt.isPresent()){
                Optional<List<TimeSlot>> optionalTimeSlot = timeSlotRepository.findByStartTimeOrEndTimeOrStartTimeBetweenAndEndTimeBetweenAndCourtId(body.getStartTime(), body.getEndTime(), body.getStartTime(), body.getEndTime(), body.getCourtId());
               log.info("value --> {}", optionalTimeSlot.get());
                if (optionalTimeSlot.get().isEmpty()){
                    log.info("There is no time slot matching this time slot");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
//                    LocalTime startTime = LocalTime.parse(body.getStartTime(), formatter);
//                    LocalTime endTime = LocalTime.parse(body.getEndTime(), formatter);
log.info("time slot body {}", body);
                    TimeSlot timeSlot = TimeSlot.builder()
                            .courtId(body.getCourtId())
                            .startTime(body.getStartTime())
                            .endTime(body.getEndTime())
                            .available(true)
                            .price(body.getPrice())
                            .build();
                    timeSlotRepository.save(timeSlot);
                    return CommonResponseModel.builder()
                            .status(HttpStatus.CREATED)
                            .message("Time Slot Created Successfully")
                            .build();
                }else {
                    log.info("There is time slot matching this time slot");
                    throw new CommonException("Invalid Time Slot, Time Slot Already Exists");
                }
            }else {
                throw new CommonException("Court with id " + body.getCourtId() + " does not exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public List<TimeSlot> getTimeSlotsByCourtId(Long courtId, int page, int size) throws CommonException {
        try {
            Optional<Court> optionalCourt = courtRepository.findById(courtId);
            if (optionalCourt.isPresent()){
                PageRequest pageRequest = PageRequest.of(page, size);
                Optional<List<TimeSlot>> optionalTimeSlots = timeSlotRepository.findByCourtId(courtId, pageRequest);
                if (optionalTimeSlots.isPresent()){
                    return optionalTimeSlots.get();
                }else {
                    throw new CommonException("No Court is Found");
                }
            }else {
                throw new CommonException("Court with id " + courtId + " does not exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel updateTimeSlot(Long timeSlotId, TimeSlotRequest body) throws CommonException {
        try {
            Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
            if (optionalTimeSlot.isPresent()){
                Optional<List<TimeSlot>> checkTimeSlotExist = timeSlotRepository.findByStartTimeOrEndTimeOrStartTimeBetweenAndEndTimeBetweenAndCourtId(body.getStartTime(), body.getEndTime(), body.getStartTime(), body.getEndTime(), body.getCourtId());
                if (checkTimeSlotExist.get().isEmpty()){
                    optionalTimeSlot.get().setStartTime(body.getStartTime());
                    optionalTimeSlot.get().setEndTime(body.getEndTime());
                    optionalTimeSlot.get().setAvailable(body.getAvailable());
                    optionalTimeSlot.get().setPrice(body.getPrice());
                    timeSlotRepository.save(optionalTimeSlot.get());
                    return CommonResponseModel.builder()
                            .status(HttpStatus.OK)
                            .message("Time Slot Updated Successfully")
                            .build();
                }else {
                    throw new CommonException("Invalid Time Slot, Time Slot Already Exists");
                }
            }else {
                throw new CommonException("Time Slot with id " + timeSlotId + " does not exist");
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public CommonResponseModel deleteTimeSlot(Long timeSlotId) throws CommonException {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (optionalTimeSlot.isPresent()){
            timeSlotRepository.deleteById(timeSlotId);
            return CommonResponseModel.builder()
                    .status(HttpStatus.OK)
                    .message("Time Slot Deleted Successfully")
                    .build();
        }else {
            throw new CommonException("Time Slot with id " + timeSlotId + " does not exist");
        }
    }
}
