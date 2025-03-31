package com.core.elevator_api.service;

import com.core.elevator_api.dto.ElevatorCallDto;

import java.util.List;

public interface KeycardAuthService {

  List<ElevatorCallDto> getAuthorizedPublicElevatorCalls(List<ElevatorCallDto> elevatorCalls, List<ElevatorCallDto> accessDeniedRequests,
                                                         List<ElevatorCallDto> restrictedAccessGranted, String requestId);

  List<ElevatorCallDto> getAuthorizedfreightElevatorCalls(List<ElevatorCallDto> elevatorCalls, List<ElevatorCallDto> accessDeniedRequests,
                                                          List<ElevatorCallDto> restrictedAccessGranted, String requestId);
}
