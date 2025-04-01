package com.core.elevator_api.service.implementation;

import com.core.elevator_api.api.exception.ElevatorMaxWeightExceededException;
import com.core.elevator_api.api.exception.ErrorProcessingDataException;
import com.core.elevator_api.api.exception.MissingDataException;
import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.api.model.Floor;
import com.core.elevator_api.api.model.Incident;
import com.core.elevator_api.api.repository.ElevatorRepository;
import com.core.elevator_api.api.repository.FloorRepository;
import com.core.elevator_api.api.repository.IncidentRepository;
import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.constants.IncidentTypes;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.dto.ElevatorDto;
import com.core.elevator_api.dto.responses.ElevatorCallResponse;
import com.core.elevator_api.dto.responses.ElevatorInfoResponse;
import com.core.elevator_api.mapper.ElevatorMapper;
import com.core.elevator_api.service.ElevatorService;
import com.core.elevator_api.service.KeycardAuthService;
import com.core.elevator_api.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElevatorServiceImplementation implements ElevatorService {

  private final ElevatorRepository elevatorRepository;

  private final FloorRepository floorRepository;

  private final IncidentRepository incidentRepository;

  private final ElevatorMapper elevatorMapper;

  private final KeycardAuthService keycardAuthService;

  @Override
  @Transactional
  public ElevatorCallResponse callPublicElevator(List<ElevatorCallDto> elevatorCalls, String requestId) {
    Elevator elevator = getElevatorByType(ElevatorTypes.PUBLIC, requestId);
    return prepareElevatorCalls(elevator, ElevatorTypes.PUBLIC, elevatorCalls, requestId);
  }

  @Override
  @Transactional
  public ElevatorCallResponse callFreightElevator(List<ElevatorCallDto> elevatorCalls, String requestId) {
    Elevator elevator = getElevatorByType(ElevatorTypes.FREIGHT, requestId);
    return prepareElevatorCalls(elevator, ElevatorTypes.FREIGHT, elevatorCalls, requestId);
  }

  private Elevator getElevatorByType(ElevatorTypes elevatorType, String requestId) {
    log.info("Retrieving the : {} elevator data, requestId: {}", elevatorType.name().toLowerCase(), requestId);
    Optional<Elevator> elevatorOptional = elevatorRepository.findByType(elevatorType);
    if (elevatorOptional.isEmpty()) {
      throw new MissingDataException("Couldn't find elevator with type: " + elevatorType);
    }
    return elevatorOptional.get();
  }

  private ElevatorCallResponse prepareElevatorCalls(Elevator elevator, ElevatorTypes elevatorType,
                                                    List<ElevatorCallDto> elevatorCalls, String requestId) {
    ElevatorDto elevatorDto = elevatorMapper.mapElevatorToDto(elevator);
    elevatorDto.setCurrentWeight(0);
    List<ElevatorCallDto> accessDeniedRequests = new ArrayList<>();
    List<ElevatorCallDto> restrictedAccessGranted = new ArrayList<>();
    List<ElevatorCallDto> authorizedElevatorCalls =
      getAuthorizedCalls(elevatorCalls, accessDeniedRequests, restrictedAccessGranted, elevatorType, requestId);
    List<String> trajectory = new ArrayList<>();
    if (!authorizedElevatorCalls.isEmpty()) {
      try {
        trajectory = processElevatorCalls(authorizedElevatorCalls, elevatorDto, requestId);
      } catch (MissingDataException | ElevatorMaxWeightExceededException ex) {
        throw new ErrorProcessingDataException(ex.getMessage());
      }
    }
    return ElevatorCallResponse.builder()
      .trajectory(trajectory)
      .accessDeniedRequests(accessDeniedRequests)
      .restrictedAccessGranted(restrictedAccessGranted)
      .currentFloor(elevatorDto.getCurrentFloor())
      .build();
  }

  private List<ElevatorCallDto> getAuthorizedCalls(List<ElevatorCallDto> elevatorCalls, List<ElevatorCallDto> accessDeniedRequests,
                                                   List<ElevatorCallDto> restrictedAccessGranted, ElevatorTypes elevatorType, String requestId) {
    List<ElevatorCallDto> authorizedElevatorCalls;
    if (ElevatorTypes.PUBLIC.equals(elevatorType)) {
      authorizedElevatorCalls =
        keycardAuthService.getAuthorizedPublicElevatorCalls(elevatorCalls, accessDeniedRequests, restrictedAccessGranted, requestId);
    } else {
      authorizedElevatorCalls =
        keycardAuthService.getAuthorizedfreightElevatorCalls(elevatorCalls, accessDeniedRequests, restrictedAccessGranted, requestId);
    }
    return authorizedElevatorCalls;
  }

  private List<String> processElevatorCalls(List<ElevatorCallDto> elevatorCalls, ElevatorDto elevatorDto, String requestId) {
    log.info("Processing elevator calls, requestId: {}", requestId);
    List<String> trajectory = new ArrayList<>();

    trajectory.add("Elevator starting at floor: " + elevatorDto.getCurrentFloor());

    while (!elevatorCalls.isEmpty()) {
      ElevatorCallDto nextCall = findMaxPriorityCall(elevatorCalls, elevatorDto);
      elevatorDto.setCurrentWeight(getElevatorCurrentWeight(elevatorDto, nextCall));

      Integer newCurrentFloor = moveElevator(elevatorDto, nextCall);
      updateTrajectory(trajectory, nextCall);
      elevatorDto.setCurrentFloor(newCurrentFloor);
      elevatorDto.setCurrentWeight(elevatorDto.getCurrentWeight() - nextCall.getWeight());
      elevatorCalls.remove(nextCall);
    }

    trajectory.add("Elevator ended at floor: " + elevatorDto.getCurrentFloor());

    trajectory.forEach(log::info);
    updateElevatorState(elevatorDto, requestId);

    return trajectory;
  }
  private void updateTrajectory(List<String> trajectory, ElevatorCallDto elevatorCall) {
    trajectory.add("Elevator reached floor: " + elevatorCall.getDestinationFloor());
    trajectory.add("Completed request with currentFloor: " + elevatorCall.getCurrentFloor()
      + " and destinationFloor: " + elevatorCall.getDestinationFloor());
  }

  private ElevatorCallDto findMaxPriorityCall(List<ElevatorCallDto> elevatorCalls, ElevatorDto elevator) {
    boolean isAscending = elevator.getCurrentFloor() < getHighestFloor(elevatorCalls, elevator);
    ElevatorCallDto maxPriorityCall;
    if (isAscending) {
      maxPriorityCall = Collections.min(elevatorCalls, Comparator.comparingInt(ElevatorCallDto::getCurrentFloor));
    } else {
      maxPriorityCall = Collections.max(elevatorCalls, Comparator.comparingInt(ElevatorCallDto::getCurrentFloor));
    }
    return maxPriorityCall;
  }

  private Integer getHighestFloor(List<ElevatorCallDto> elevatorCalls, ElevatorDto elevator) {
    return elevatorCalls.stream().mapToInt(ElevatorCallDto::getCurrentFloor).max().orElse(elevator.getCurrentFloor());
  }
  private Integer moveElevator(ElevatorDto elevator, ElevatorCallDto elevatorCall) {
    Integer currentFloor = elevator.getCurrentFloor();
    Integer destinationFloor = elevatorCall.getDestinationFloor();
    while (!currentFloor.equals(destinationFloor)) {
      if (currentFloor < destinationFloor) {
        currentFloor++;
      } else {
        currentFloor--;
      }
    }
    return currentFloor;
  }

  private Integer getElevatorCurrentWeight(ElevatorDto elevator, ElevatorCallDto elevatorCall) {
    Integer elevatorMaxSupportedWeight = elevator.getMaxSupportedWeight();
    Integer elevatorCurrentWeight = elevator.getCurrentWeight() + elevatorCall.getWeight();
    if (elevatorCurrentWeight > elevatorMaxSupportedWeight) {
      throw new ElevatorMaxWeightExceededException(createAndGetIncident(elevator, elevatorCall));
    }
    return elevatorCurrentWeight;
  }

  private void updateElevatorState(ElevatorDto elevatorDto, String requestId) {
    log.info("Updating and saving the new elevator state for requestId: {}", requestId);
    Elevator elevator = elevatorMapper.mapElevatorDtoToEntity(elevatorDto);
    elevator.setCurrentFloor(elevatorDto.getCurrentFloor());
    elevator.setLastUpdatedAt(LocalDateTime.now());
    elevatorRepository.save(elevator);
  }

  private String createAndGetIncident(ElevatorDto elevatorDto, ElevatorCallDto elevatorCall) {
    Floor floor = floorRepository.findByNumber(elevatorDto.getCurrentFloor())
      .orElseThrow(() -> new MissingDataException("No floor found for floorNumber: " + elevatorDto.getCurrentFloor()));
    Elevator elevator = elevatorMapper.mapElevatorDtoToEntity(elevatorDto);

    String incidentMessage = MessageUtil.createElevatorWeightExceededMessage(
      elevatorDto.getType().name().toLowerCase(), elevatorCall.getCurrentFloor(),
      elevatorDto.getMaxSupportedWeight(), elevatorDto.getCurrentWeight());

    incidentRepository.save(Incident.builder()
      .id(UUID.randomUUID().toString())
      .type(IncidentTypes.MAX_WEIGHT_EXCEEDED)
      .description(incidentMessage)
      .elevator(elevator)
      .floor(floor)
      .time(LocalDateTime.now())
      .createdAt(LocalDateTime.now())
      .build());

    return incidentMessage;
  }

  @Override
  public ElevatorInfoResponse getElevatorsInfo(String id, ElevatorTypes type, String requestId) {
    log.info("Getting elevators info, requestId: {}", requestId);
    return ElevatorInfoResponse.builder()
      .elevators(Strings.isBlank(id) && Objects.isNull(type)
        ? elevatorRepository.findAll()
        : elevatorRepository.findByIdOrType(id, type))
      .build();
  }

}
