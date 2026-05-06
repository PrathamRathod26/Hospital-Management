package com.project.HospitalManagement.Records;

public class UserDto {

    public record auth(
            Long id,
            String accessToken,
            String RefreshToken
    ){}

    public record response(
            Long id,
            String email
    ){}

    public record refreshRequest(
            String refreshToken
    ){}

    public record loginRequest(
            String email,
            String password
    ){}

    public record registerRequest (
            String email,
            String password
    ){}
}
