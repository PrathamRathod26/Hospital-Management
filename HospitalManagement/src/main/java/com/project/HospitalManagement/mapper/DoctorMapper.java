package com.project.HospitalManagement.mapper;

import com.project.HospitalManagement.Records.DoctorDto;
import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.DoctorSchedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorDto.response EntityToRecord(Doctor doctor);
    DoctorDto.response DbToRecord(DoctorDto.dbResponse doctor);
    Doctor RecordToEntity(DoctorDto.registerRequest dto);

    DoctorDto.scheduleResponse ScheduleEntityToRecord(DoctorSchedule schedule);
    DoctorDto.scheduleResponse ScheduleDbToRecord(DoctorDto.dbScheduleResponse dto);
    DoctorSchedule ScheduleRecordToEntity(DoctorDto.scheduleResponse dto);
}
