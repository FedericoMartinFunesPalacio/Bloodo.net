package com.FedericoFunes.app_service.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Generic exceptions
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(ex.getStatusCode().value())
                .description(ex.getReason() != null ? ex.getReason() : ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGenericException(Exception ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .description(ex.getMessage() != null ? ex.getMessage() : "Internal server error")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //Personalized exceptions
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBadRequestException(BadRequestException ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(ex.getMessage() != null ? ex.getMessage() : "Bad request")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleNotFoundException(NotFoundException ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .description(ex.getMessage() != null ? ex.getMessage() : "Not found")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponseDTO> handleForbiddenException(ForbiddenException ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .description(ex.getMessage() != null ? ex.getMessage() : "Not allowed")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
        ExceptionResponseDTO response = ExceptionResponseDTO.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .description(ex.getMessage() != null ? ex.getMessage() : "Not authorized")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
