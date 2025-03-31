package com.core.elevator_api.api.exception;

public class ErrorProcessingDataException extends RuntimeException {

  private Throwable rootCause;
  public ErrorProcessingDataException(String message) {
    super(message);
  }

  public ErrorProcessingDataException(String message, Throwable rootCause) {
    super(message);
    this.rootCause = rootCause;
  }

}
