/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ErrorDetails> invalidFilterException(InvalidFilterException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingInputException.class)
    public ResponseEntity<ErrorDetails> missingInputException(MissingInputException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> resourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        var error = new ErrorDetails(
                "Validation Failed: " + String.join(", ", errors),
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ErrorDetails> invalidFileTypeException(InvalidFileTypeException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MinioOperationException.class)
    public ResponseEntity<?> minioOperationException(MinioOperationException ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*@ExceptionHandler({AccessDeniedException.class, CSRFException.class})
    public ResponseEntity<ErrorDetails> handleSecurityException(Exception ex, HttpServletRequest request) {
        var error = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                new Date(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }*/
}