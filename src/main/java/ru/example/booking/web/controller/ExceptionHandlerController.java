package ru.example.booking.web.controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.web.model.defaults.ErrorResponse;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EntityAlreadyExists.class)
    public ResponseEntity<ErrorResponse> alreadyExistsException(EntityAlreadyExists e) {
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> argumentsNotValid(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<String> allErrors = result.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String error = String.join("; ", allErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error));
    }

    @ExceptionHandler(RoomBookingException.class)
    public ResponseEntity<ErrorResponse> bookingException(RoomBookingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> incorrectParam(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Is an incorrect param"));
    }
}
