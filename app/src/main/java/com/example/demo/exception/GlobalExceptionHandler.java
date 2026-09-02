package com.example.demo.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            int status,
            String error,
            String message,
            Instant timestamp
    ) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        Throwable rootCause = ex.getRootCause();

        // Catch custom PL/SQL business exception ORA-20001
        if (rootCause instanceof SQLException sqlEx) {
            if (sqlEx.getErrorCode() == 20001 || (sqlEx.getMessage() != null && sqlEx.getMessage().contains("ORA-20001"))) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "BUSINESS_RULE_VIOLATION",
                        sqlEx.getMessage(),
                        Instant.now()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        // Temporary debug: Expose actual underlying database message
        String debugMessage = (rootCause != null) ? rootCause.getMessage() : ex.getMessage();

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "DATABASE_ERROR",
                debugMessage, // <-- Exposes exact ORA error in Postman response
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}