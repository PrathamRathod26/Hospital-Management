package com.project.HospitalManagement.exception;

import com.project.HospitalManagement.Records.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto.response> handleNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto.response(404,"Not Found", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDto.response> handleDuplicateResources(DuplicateResourceException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDto.response(409,"Conflict", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto.response> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto.response(400,"Validation Failed", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto.response> handleBadCredentials(BadCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto.response(401,"Unauthorized", "You are not authorized to access this page", LocalDateTime.now()));
    }

//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<ErrorDto.response> handleGeneric(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ErrorDto.response(500,"Internal Server Error", "Something went wrong", LocalDateTime.now()));
//    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto.response> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {

        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        Class<?> requiredType = ex.getRequiredType();

        String message;
        if (requiredType != null && requiredType.isEnum()) {
            String validValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message = String.format("Invalid value '%s'. Accepted values are: [%s]", invalidValue, validValues);
        } else {
            message = String.format("Invalid value '%s' for parameter '%s'", invalidValue, ex.getName());
        }

        ErrorDto.response error = new ErrorDto.response(400,"Bad Request",message, LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDto.response> handleIllegalStateException(IllegalStateException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto.response(400,"Illegal State Found",ex.getMessage(), LocalDateTime.now()));
    }
    @ExceptionHandler(FileMandatoryException.class)
    public ResponseEntity<ErrorDto.response> FileMandatoryException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto.response(400,"Bad Request", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorDto.response> FileNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto.response(404,"File not found", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto.response> BadRequestException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto.response(400,"Bad Request", ex.getMessage(), LocalDateTime.now()));
    }
}
