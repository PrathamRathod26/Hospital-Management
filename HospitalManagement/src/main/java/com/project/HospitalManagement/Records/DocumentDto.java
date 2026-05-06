package com.project.HospitalManagement.Records;

public class DocumentDto {
    public record response(
            Long id,
            String fileName
    ){}

    public record responseWithUrl(
            Long id,
            String fileName,
            String documentUrl
    ){}
}
