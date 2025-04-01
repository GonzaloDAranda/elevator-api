package com.core.elevator_api.util;

import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.utils.MessageUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class MessageUtilTest {

  @Test
  void createElevatorWeightExceededMessageTest() {
    String elevatorType = ElevatorTypes.PUBLIC.name();
    Integer currentFloor = 0;
    Integer maxSupportedWeight = 1000;
    Integer currentWeight = 1200;
    String expectedMesage = "Stopping the " + elevatorType
      + " elevator at floor " + currentFloor
      + " and sounding the alarm! The maximum capacity of "
      + maxSupportedWeight + "kg was exceeded with a current load of " + currentWeight + "kg";
    String result = MessageUtil.createElevatorWeightExceededMessage(elevatorType, currentFloor, maxSupportedWeight, currentWeight);
    assertNotNull(result);
    assertEquals(expectedMesage, result);
  }
}
