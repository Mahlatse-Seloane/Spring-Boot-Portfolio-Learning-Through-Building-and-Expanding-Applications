package com.devtiro.task.controller;

import com.devtiro.task.domain.dto.response.ExceptionResponseDto;
import com.devtiro.task.domain.dto.response.ValidationExceptionResponseDto;
import com.devtiro.task.exception.DuplicateResourceException;
import com.devtiro.task.exception.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    private static final String TITLE_CONSTRAINT = "uk_tasks_title";

    // ---------- 400 ----------

    // -- 404 --
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleTaskNotFoundException(
            TaskNotFoundException ex,
            HttpServletRequest request)
    {
        log.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return responseBuilder(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // -- 400 --
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ValidationExceptionResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request)
    {
        log.warn("Validation failed at {}", request.getRequestURI());

        // LinkedHashMap keeps ordering; merge keeps every message when a field has several failures.
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.merge(
                        error.getField(),
                        String.valueOf(error.getDefaultMessage()),
                        (existing, added) -> existing.equals(added) ? existing : existing + "; " + added));

        ValidationExceptionResponseDto response = new ValidationExceptionResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request)
    {
        log.warn("Unreadable request body at {}: {}", request.getRequestURI(), ex.getMessage());
        return responseBuilder(HttpStatus.BAD_REQUEST, "Malformed JSON request", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request)
    {
        log.warn("Argument type mismatch at {}: parameter '{}'", request.getRequestURI(), ex.getName());
        return responseBuilder(
                HttpStatus.BAD_REQUEST,
                String.format("Invalid value for parameter '%s'", ex.getName()),
                request);
    }

    // ---------- 409 ----------

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request)
    {
        log.warn("Duplicate resource at {}: {}", request.getRequestURI(), ex.getMessage());
        return responseBuilder(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request)
    {
        log.warn("Unhandled data integrity violation at {}" , request.getRequestURI(), ex);

        String message = isTitleConstraint(ex)
                ? "This title already exists."
                : "A data integrity conflict occurred";

        return responseBuilder(HttpStatus.CONFLICT, message, request);
    }

    // ---------- 500 ----------

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<@NonNull ExceptionResponseDto> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request)
    {
        log.warn("Unhandled runtime exception at {}", request.getRequestURI(), ex);

        return responseBuilder(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later",
                request);
    }

    private ResponseEntity<@NonNull ExceptionResponseDto> responseBuilder(
            HttpStatus status,
            String message,
            HttpServletRequest request)
    {
        ExceptionResponseDto response = new ExceptionResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    // Walks the whole cause chain instead of only getCause(), so it still works if Spring or
    // Hibernate adds another wrapper. Lower-case + contains tolerates H2 upper-casing and
    // index-suffixed names such as "PUBLIC.UK_TASKS_TITLE_INDEX_4".
    private static boolean isTitleConstraint(Throwable ex)
    {
        for (Throwable t = ex; t != null; t = t.getCause())
        {
            if (t instanceof ConstraintViolationException cve
                    && cve.getConstraintName() != null
                    && cve.getConstraintName().toLowerCase(Locale.ROOT).contains(TITLE_CONSTRAINT))
            {
                return true;
            }
        }
        return false;
    }
}













