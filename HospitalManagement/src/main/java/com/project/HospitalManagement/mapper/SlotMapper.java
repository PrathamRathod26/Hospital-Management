package com.project.HospitalManagement.mapper;

import com.project.HospitalManagement.Records.SlotDto;
import com.project.HospitalManagement.entity.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SlotMapper {
    Slot RecordToEntity(SlotDto.request dto);

    @Mapping(target = "capacity", source = "slotCapacity")
    SlotDto.response EntityToRecord(Slot slot);
    SlotDto.response DbToRecord(SlotDto.dbResponse dto);
    SlotDto.responseWithAppointmentIdFlag DbAppointmentFlagToRecord(SlotDto.dbResponseWithAppointmentIdFlag dto);
}
