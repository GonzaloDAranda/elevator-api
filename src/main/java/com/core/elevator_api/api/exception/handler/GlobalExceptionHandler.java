package com.core.elevator_api.api.exception.handler;

import com.core.elevator_api.api.exception.ElevatorMaxWeightExceededException;
import com.core.elevator_api.api.exception.ErrorProcessingDataException;
import com.core.elevator_api.api.exception.InvalidDataException;
import com.core.elevator_api.api.exception.MissingDataException;
import com.core.elevator_api.dto.responses.CommonErrorResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String HEADER_REQUEST_ID = "requestId";
  public static final String REQUEST_CANNOT_BE_PROCESSED_DEFAULT_MESSAGE = "Request cannot be processed, requestId: {}.";

  @Override
  protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
                                                                         @NotNull HttpHeaders headers,
                                                                         @NotNull HttpStatusCode status,
                                                                         WebRequest request) {
    log.error("Handling MethodArgumentNotValidException with message: {}", ex.getMessage());
    final String requestId = request.getHeader(HEADER_REQUEST_ID);
    log.error(REQUEST_CANNOT_BE_PROCESSED_DEFAULT_MESSAGE, requestId, ex);
    final List<String> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      CommonErrorResponse.builder()
        .errorMessages(errors)
        .status(HttpStatus.BAD_REQUEST.name())
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .build()
      );
  }

  @Override
  protected @NotNull ResponseEntity<Object> handleHttpMessageNotReadable(@NotNull HttpMessageNotReadableException ex,
                                                                         @NotNull HttpHeaders headers,
                                                                         @NotNull HttpStatusCode status,
                                                                         WebRequest request) {
    log.error("Handling HttpMessageNotReadableException with message: {}", ex.getMessage());
    final String requestId = request.getHeader(HEADER_REQUEST_ID);
    log.error(REQUEST_CANNOT_BE_PROCESSED_DEFAULT_MESSAGE, requestId, ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getRootCause().getMessage()))
        .status(HttpStatus.BAD_REQUEST.name())
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .build()
    );
  }

  @ExceptionHandler(HttpStatusCodeException.class)
  protected ResponseEntity<Object> handleHttpStatusCode(HttpStatusCodeException ex, WebRequest request) {
    log.error("Handling HttpMessageNotReadableException with message: {}", ex.getMessage());
    final String requestId = request.getHeader(HEADER_REQUEST_ID);
    log.error(REQUEST_CANNOT_BE_PROCESSED_DEFAULT_MESSAGE, requestId, ex);
    return ResponseEntity.status(ex.getStatusCode()).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getRootCause().getMessage()))
        .status(ex.getStatusText())
        .statusCode(ex.getStatusCode().value())
        .build()
    );
  }

  @ExceptionHandler(InvalidDataException.class)
  protected ResponseEntity<Object> handleInvalidData(InvalidDataException ex) {
    log.error("Handling InvalidDataException with message: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getMessage()))
        .status(HttpStatus.BAD_REQUEST.name())
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .build()
    );
  }

  @ExceptionHandler(ErrorProcessingDataException.class)
  protected ResponseEntity<Object> handleErrorProcessingData(ErrorProcessingDataException ex) {
    log.error("Handling ErrorProcessingDataException with message: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getMessage()))
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build()
    );
  }

  @ExceptionHandler(MissingDataException.class)
  protected ResponseEntity<Object> handleMissingData(MissingDataException ex) {
    log.error("Handling MissingDataException with message: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getMessage()))
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build()
    );
  }

  @ExceptionHandler(ElevatorMaxWeightExceededException.class)
  protected ResponseEntity<Object> handleElevatorMaxWeightExceeded(ElevatorMaxWeightExceededException ex) {
    log.error("Handling ElevatorMaxWeightExceededException with message: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      CommonErrorResponse.builder()
        .errorMessages(List.of(ex.getMessage()))
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build()
    );
  }

}
