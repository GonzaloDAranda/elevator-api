package com.core.elevator_api.service;

import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.dto.responses.ElevatorCallResponse;
import com.core.elevator_api.dto.responses.ElevatorInfoResponse;

import java.util.List;

public interface ElevatorService {

  ElevatorCallResponse callPublicElevator(List<ElevatorCallDto> elevatorCalls, String requestId);

  ElevatorCallResponse callFreightElevator(List<ElevatorCallDto> elevatorCalls, String requestId);

  ElevatorInfoResponse getElevatorsInfo(String id, ElevatorTypes type, String requestId);
}
