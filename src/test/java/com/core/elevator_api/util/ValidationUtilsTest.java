package com.core.elevator_api.util;

import com.core.elevator_api.api.exception.InvalidDataException;
import com.core.elevator_api.dto.requests.ElevatorCall;
import com.core.elevator_api.dto.requests.ElevatorCallRequest;
import com.core.elevator_api.utils.ValidatorUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ValidationUtilsTest {

  @Test
  void validateElevatorCallRequestThrows() {
    assertThrows(InvalidDataException.class,
      () -> ValidatorUtil.validateElevatorCallRequest(null, UUID.randomUUID().toString()));
  }

  @Test
  void validateElevatorCallRequestSuccess() {
    ElevatorCall elevatorCall = ElevatorCall.builder().currentFloor(0).destinationFloor(1).build();
    List<ElevatorCall> elevatorCalls = new ArrayList<>();
    elevatorCalls.add(elevatorCall);
    ElevatorCallRequest request = ElevatorCallRequest.builder().elevatorCalls(elevatorCalls).build();
    assertDoesNotThrow(() -> ValidatorUtil.validateElevatorCallRequest(request, UUID.randomUUID().toString()));
  }

  @Test
  void validateCallFloorsTest() {
    ElevatorCall elevatorCall = ElevatorCall.builder().currentFloor(0).destinationFloor(0).build();
    List<ElevatorCall> elevatorCalls = new ArrayList<>();
    elevatorCalls.add(elevatorCall);
    ValidatorUtil.validateCallFloors(elevatorCalls);
    assertTrue(elevatorCalls.isEmpty());
  }
}
