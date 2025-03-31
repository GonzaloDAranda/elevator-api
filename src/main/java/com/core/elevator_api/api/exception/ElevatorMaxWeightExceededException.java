package com.core.elevator_api.api.exception;

public class ElevatorMaxWeightExceededException extends RuntimeException {
  public ElevatorMaxWeightExceededException(String message) {
    super(message);
  }

}
