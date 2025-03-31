package com.core.elevator_api.api.controller;

import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.dto.requests.ElevatorCallRequest;
import com.core.elevator_api.dto.responses.ElevatorCallResponse;
import com.core.elevator_api.dto.responses.ElevatorInfoResponse;
import com.core.elevator_api.mapper.ElevatorMapper;
import com.core.elevator_api.service.ElevatorService;
import com.core.elevator_api.utils.ValidatorUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class ElevatorController {

  private final ElevatorService elevatorService;

  private final ElevatorMapper elevatorMapper;


  @Autowired
  public ElevatorController(ElevatorService elevatorService, ElevatorMapper elevatorMapper) {
    this.elevatorService = elevatorService;
    this.elevatorMapper = elevatorMapper;
  }

  @PostMapping(value = "/callPublicElevator",
    produces = { "application/json" },
    consumes = { "application/json" })
  public ResponseEntity<ElevatorCallResponse> callPublicElevator(@Valid @RequestBody ElevatorCallRequest request,
                                                                 @RequestParam(value = "requestId") String requestId) {
    log.info("Calling the public elevator, requestId: {}", requestId);
    ValidatorUtil.validateElevatorCallRequest(request, requestId);
    List<ElevatorCallDto> elevatorCalls = elevatorMapper.mapElevatorCallRequest(request.getElevatorCalls());
    ElevatorCallResponse response = elevatorService.callPublicElevator(elevatorCalls, requestId);
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/callFreightElevator",
    produces = { "application/json" },
    consumes = { "application/json" })
  public ResponseEntity<ElevatorCallResponse> callFreightElevator(@RequestBody ElevatorCallRequest request,
                                                                  @RequestParam(value = "requestId") String requestId) {
    log.info("Calling the freight elevator, requestId: {}", requestId);
    ValidatorUtil.validateElevatorCallRequest(request, requestId);
    List<ElevatorCallDto> elevatorCalls = elevatorMapper.mapElevatorCallRequest(request.getElevatorCalls());
    ElevatorCallResponse response = elevatorService.callFreightElevator(elevatorCalls, requestId);
    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/getElevatorsInfo",
    produces = { "application/json" })
  public ResponseEntity<ElevatorInfoResponse> getElevatorsInfo(@RequestParam(value = "id", required = false) String id,
                                                               @RequestParam(value = "type", required = false) ElevatorTypes type,
                                                               @RequestParam(value = "requestId") String requestId) {
    log.info("Requesting elevators info, requestId: {}", requestId);
    ElevatorInfoResponse response = elevatorService.getElevatorsInfo(id, type, requestId);
    if (Objects.isNull(response) || response.getElevators().isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(response);
  }

}
