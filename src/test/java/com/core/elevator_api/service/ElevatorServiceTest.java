package com.core.elevator_api.service;

import com.core.elevator_api.api.exception.ErrorProcessingDataException;
import com.core.elevator_api.api.exception.MissingDataException;
import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.api.model.Floor;
import com.core.elevator_api.api.repository.ElevatorRepository;
import com.core.elevator_api.api.repository.FloorRepository;
import com.core.elevator_api.api.repository.IncidentRepository;
import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.dto.responses.ElevatorCallResponse;
import com.core.elevator_api.dto.responses.ElevatorInfoResponse;
import com.core.elevator_api.mapper.ElevatorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ElevatorServiceTest {

  @MockitoBean
  private ElevatorRepository elevatorRepository;

  @MockitoBean
  private FloorRepository floorRepository;

  @MockitoBean
  private IncidentRepository incidentRepository;

  @Autowired
  private ElevatorMapper elevatorMapper;

  @MockitoBean
  private KeycardAuthService keycardAuthService;

  @Autowired
  private ElevatorService elevatorService;

  private final String requestId = UUID.randomUUID().toString();
  private final String publicElevatorId = UUID.randomUUID().toString();
  private final String freightElevatorId = UUID.randomUUID().toString();

  @Test
  void callPublicElevatorSuccess() {
    List<ElevatorCallDto> elevatorCalls = new ArrayList<>(getElevatorCalls());
    Optional<Elevator> elevator = Optional.of(Elevator.builder()
      .type(ElevatorTypes.PUBLIC)
      .id(UUID.randomUUID().toString())
      .maxSupportedWeight(1000)
      .currentFloor(0)
      .build());
    when(this.elevatorRepository.findByType(ElevatorTypes.PUBLIC)).thenReturn(elevator);
    when(this.keycardAuthService.getAuthorizedPublicElevatorCalls(anyList(), anyList(), anyList(), any())).thenReturn(elevatorCalls);
    ElevatorCallResponse response = this.elevatorService.callPublicElevator(elevatorCalls, requestId);
    assertNotNull(response);
    assertFalse(CollectionUtils.isEmpty(response.getTrajectory()));
    assertEquals(-1, response.getCurrentFloor());
  }

  @Test
  void callFreightElevatorSuccess() {
    List<ElevatorCallDto> elevatorCalls = new ArrayList<>(getElevatorCalls());
    Optional<Elevator> elevator = Optional.of(Elevator.builder()
      .type(ElevatorTypes.FREIGHT)
      .id(UUID.randomUUID().toString())
      .maxSupportedWeight(3000)
      .currentFloor(0)
      .build());
    when(this.elevatorRepository.findByType(ElevatorTypes.FREIGHT)).thenReturn(elevator);
    when(this.keycardAuthService.getAuthorizedfreightElevatorCalls(anyList(), anyList(), anyList(), any())).thenReturn(elevatorCalls);
    ElevatorCallResponse response = this.elevatorService.callFreightElevator(elevatorCalls, requestId);
    assertNotNull(response);
    assertFalse(CollectionUtils.isEmpty(response.getTrajectory()));
    assertEquals(-1, response.getCurrentFloor());
  }

  @Test
  void getElevatorsAllElevators() {
    List<Elevator> elevators = getElevators();
    when(this.elevatorRepository.findAll()).thenReturn(elevators);
    ElevatorInfoResponse response = this.elevatorService.getElevatorsInfo(null, null, requestId);
    assertNotNull(response);
    assertEquals(elevators.get(0), response.getElevators().get(0));
    assertEquals(elevators.get(1), response.getElevators().get(1));
  }

  @Test
  void getElevatorsByIdAndType() {
    List<Elevator> elevators = List.of(Elevator.builder().id(publicElevatorId).type(ElevatorTypes.PUBLIC).build());
    when(this.elevatorRepository.findByIdOrType(publicElevatorId, ElevatorTypes.PUBLIC)).thenReturn(elevators);
    ElevatorInfoResponse response = this.elevatorService.getElevatorsInfo(publicElevatorId, ElevatorTypes.PUBLIC, requestId);
    assertNotNull(response);
    assertEquals(1, response.getElevators().size());
    assertEquals(elevators.get(0), response.getElevators().get(0));
  }

  @Test
  void elevatorMaxWeightExceeded() {
    List<ElevatorCallDto> elevatorCalls = new ArrayList<>(getElevatorCalls());
    elevatorCalls.get(0).setWeight(1001);
    Optional<Elevator> elevator = Optional.of(Elevator.builder()
      .type(ElevatorTypes.PUBLIC)
      .id(UUID.randomUUID().toString())
      .maxSupportedWeight(1000)
      .currentFloor(0)
      .build());
    when(this.elevatorRepository.findByType(ElevatorTypes.PUBLIC)).thenReturn(elevator);
    when(this.keycardAuthService.getAuthorizedPublicElevatorCalls(anyList(), anyList(), anyList(), any())).thenReturn(elevatorCalls);
    when(this.floorRepository.findByNumber(any())).thenReturn(Optional.of(Floor.builder().number(0).build()));
    assertThrows(ErrorProcessingDataException.class,
      () -> this.elevatorService.callPublicElevator(elevatorCalls, requestId));
  }

  private List<ElevatorCallDto> getElevatorCalls() {
    ElevatorCallDto call1 = ElevatorCallDto.builder().weight(70).currentFloor(0).destinationFloor(1).build();
    ElevatorCallDto call2 = ElevatorCallDto.builder().weight(80).currentFloor(-1).destinationFloor(1).build();
    ElevatorCallDto call3 = ElevatorCallDto.builder().weight(120).currentFloor(49).destinationFloor(0).build();
    ElevatorCallDto call4 = ElevatorCallDto.builder().weight(400).currentFloor(40).destinationFloor(-1).build();
    ElevatorCallDto call5 = ElevatorCallDto.builder().weight(500).currentFloor(10).destinationFloor(49).build();
    return List.of(call1, call2, call3, call4, call5);
  }

  private List<Elevator> getElevators() {
    Elevator publicElevator = Elevator.builder()
      .id(publicElevatorId)
      .type(ElevatorTypes.PUBLIC)
      .build();
    Elevator freightElevator = Elevator.builder()
      .id(freightElevatorId)
      .type(ElevatorTypes.FREIGHT)
      .build();
    return List.of(publicElevator, freightElevator);
  }
}
