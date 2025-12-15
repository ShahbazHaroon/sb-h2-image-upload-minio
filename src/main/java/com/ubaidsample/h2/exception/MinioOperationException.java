/*
 * @author Muhammad Ubaid Ur Raheem Ahmad AKA Shahbaz Haroon
 * Email: shahbazhrn@gmail.com
 * Cell: +923002585925
 * GitHub: https://github.com/ShahbazHaroon
 */

package com.ubaidsample.h2.exception;

import java.io.Serial;

public class MinioOperationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MinioOperationException(String message) { super(message); }

    public MinioOperationException(String message, Throwable cause) { super(message, cause); }
}