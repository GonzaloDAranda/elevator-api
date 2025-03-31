package com.core.elevator_api.utils;

import com.core.elevator_api.api.exception.InvalidDataException;
import com.core.elevator_api.dto.requests.ElevatorCallRequest;
import com.core.elevator_api.dto.requests.ElevatorCall;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@UtilityClass
@Slf4j
public class ValidatorUtil {

  private final String EMPTY_OR_INCOMPLETE_REQUEST = "The request is empty or is incomplete";
  public static void validateElevatorCallRequest(ElevatorCallRequest request, String requestId) {
    if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getElevatorCalls())) {
      log.error(EMPTY_OR_INCOMPLETE_REQUEST + " requestId: {}", requestId);
      throw new InvalidDataException(EMPTY_OR_INCOMPLETE_REQUEST);
    }
    validateCallFloors(request.getElevatorCalls());
  }

  public static void validateCallFloors(List<ElevatorCall> elevatorCalls) {
    elevatorCalls.removeIf(call -> Objects.isNull(call.getCurrentFloor()) || Objects.isNull(call.getDestinationFloor()));
  }
}
