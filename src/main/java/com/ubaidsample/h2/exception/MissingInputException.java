/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingInputException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MissingInputException(String message){
        super(message);
    }

    public MissingInputException(String message, Throwable cause) {
        super(message, cause);
    }
}