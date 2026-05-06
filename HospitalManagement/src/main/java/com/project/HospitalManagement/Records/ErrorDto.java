package com.project.HospitalManagement.Records;

import java.time.LocalDateTime;

public class ErrorDto {
    public record response(
            Integer status,
            String error,
            String message,
            LocalDateTime timestamp
    ){}
}
