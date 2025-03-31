package com.core.elevator_api.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class MessageUtil {

  public static String createElevatorWeightExceededMessage(String elevatorType, Integer currentFloor,
                                                           Integer maxSupportedWeight, Integer currentWeight) {
    return "Stopping the " + elevatorType
      + " elevator at floor " + currentFloor
      + " and sounding the alarm! The maximum capacity of "
      + maxSupportedWeight + "kg was exceeded with a current load of " + currentWeight + "kg";
  }
}
