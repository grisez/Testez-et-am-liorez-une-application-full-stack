package com.openclassrooms.starterjwt.exception;

import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Void> handleNumberFormatException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<MessageResponse> handleEmailAlreadyTakenException(EmailAlreadyTakenException e) {
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }
}
