package com.project.HospitalManagement.config;

import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class AuthUtil {
    @Value("${jwt.accessKey}")
    private String jwtSecretKey;

    @Value("${jwt.accessKeyExpirationMs}")
    private long jwtSecretKeyExpirationMs;

    @Value("${jwt.refreshKey}")
    private String jwtSecretRefreshKey;

    @Value("${jwt.refreshKeyExpirationMs}")
    private long jwtSecretRefreshKeyExpirationMs;


    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        var builder = Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole());

//        if(user.getPatientProfile() != null){
//            builder.claim("patientId", user.getPatientProfile().getId());
//        }
//        if(user.getDoctorProfile()!=null){
//            builder.claim("doctorId", user.getDoctorProfile().getId());
//        }

        return builder.issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtSecretKeyExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretRefreshKey(){
        return Keys.hmacShaKeyFor(jwtSecretRefreshKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtSecretRefreshKeyExpirationMs))
                .signWith(getSecretRefreshKey())
                .compact();
    }

    public boolean validateAccessToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public void validateRefreshToken(String token){
        Jwts.parser()
                .verifyWith(getSecretRefreshKey())
                .build()
                .parseSignedClaims(token);
    }

    public Role extractRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("role", String.class);

        if (role != null && !role.isEmpty()) {
            return Role.valueOf(role);
        }

        return null;
    }

    public ResponseCookie generateCookie(String refreshToken){
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }
}
