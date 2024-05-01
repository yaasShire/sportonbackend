package com.sporton.SportOn.service.timeSlotService;

import com.sporton.SportOn.entity.TimeSlot;
import com.sporton.SportOn.exception.commonException.CommonException;
import com.sporton.SportOn.model.CommonResponseModel;
import com.sporton.SportOn.model.timeSlotModel.TimeSlotRequest;

import java.util.List;

public interface TimeSlotService {
    CommonResponseModel createTimeSlot(TimeSlotRequest body) throws CommonException;

    List<TimeSlot> getTimeSlotsByCourtId(Long courtId, int page, int size) throws CommonException;

    CommonResponseModel updateTimeSlot(Long timeSlotId, TimeSlotRequest body) throws CommonException;

    CommonResponseModel deleteTimeSlot(Long timeSlotId) throws CommonException;
}
