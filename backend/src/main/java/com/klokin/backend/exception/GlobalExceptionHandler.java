package com.klokin.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// One place that turns our exceptions into clean JSON error responses for every controller.
// ProblemDetail is Spring's built-in standard error format (RFC 9457):
// {"status":400,"title":"Bad Request","detail":"Company name is required"}
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Something doesn't exist -> 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // A factory rejected the input -> 400 Bad Request, with the factory's message as the reason
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
