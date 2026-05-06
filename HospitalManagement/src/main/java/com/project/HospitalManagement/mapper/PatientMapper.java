package com.project.HospitalManagement.mapper;

import com.project.HospitalManagement.Records.PatientDto;
import com.project.HospitalManagement.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDto.response EntityToRecord(Patient patient);
    PatientDto.response DbToRecord(PatientDto.dbResponse patient);
    Patient RecordToEntity(PatientDto.registerRequest dto);
}
