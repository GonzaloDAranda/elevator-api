package com.core.elevator_api.service.implementation;

import com.core.elevator_api.api.exception.MissingDataException;
import com.core.elevator_api.api.model.Floor;
import com.core.elevator_api.api.model.Keycard;
import com.core.elevator_api.api.model.Person;
import com.core.elevator_api.api.repository.FloorRepository;
import com.core.elevator_api.api.repository.KeycardRepository;
import com.core.elevator_api.api.repository.PersonRepository;
import com.core.elevator_api.constants.AccessLevels;
import com.core.elevator_api.constants.PrivilegeLevels;
import com.core.elevator_api.constants.Roles;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.service.KeycardAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycardAuthServiceImplementation implements KeycardAuthService {

  private final KeycardRepository keycardRepository;

  private final FloorRepository floorRepository;

  private final PersonRepository personRepository;

  @Override
  public List<ElevatorCallDto> getAuthorizedPublicElevatorCalls(List<ElevatorCallDto> elevatorCalls, List<ElevatorCallDto> accessDeniedRequests,
                                                                List<ElevatorCallDto> restrictedAccessGranted, String requestId) {
    log.info("Validating access for the public elevator calls, requestId: {}", requestId);
    Map<String, Integer> restrictedFloors = floorRepository.findByAccessLevel(AccessLevels.RESTRICTED)
      .stream().collect(Collectors.toMap(Floor::getId, Floor::getNumber));
    return new ArrayList<>(elevatorCalls.stream()
      .filter(call -> {
        boolean isRestrictedFloor = restrictedFloors.containsValue(call.getDestinationFloor());
        if (!isRestrictedFloor) {
          return true;
        }
        return checkKeycardAuthorization(call, accessDeniedRequests, restrictedAccessGranted, requestId);
      }).toList()
    );
  }

  @Override
  public List<ElevatorCallDto> getAuthorizedfreightElevatorCalls(List<ElevatorCallDto> elevatorCalls,
                                                                 List<ElevatorCallDto> accessDeniedRequests,
                                                                 List<ElevatorCallDto> restrictedAccessGranted, String requestId) {
    log.info("Validating access for the public elevator calls, requestId: {}", requestId);
    Set<String> authorizedRoles = Set.of(Roles.TECHNICIAN.name(), Roles.MANAGER.name());
    return new ArrayList<>(elevatorCalls.stream()
      .filter(call -> {
        if (Strings.isBlank(call.getPersonId())) {
          return denyAccess(call, accessDeniedRequests, requestId);
        }
        try {
          String role = getPerson(call, requestId).getRole();
          return authorizedRoles.contains(role) ? grantAccess(call, restrictedAccessGranted, requestId)
            : denyAccess(call, accessDeniedRequests, requestId);
        } catch (MissingDataException ex) {
          log.error("Person not found for personId: {}, requestId: {}", call.getPersonId(), requestId);
          return false;
        }
      }).toList()
    );
  }

  private boolean checkKeycardAuthorization(ElevatorCallDto call, List<ElevatorCallDto> accessDeniedRequests,
                                            List<ElevatorCallDto> restrictedAccessGranted, String requestId) {
    log.info("Checking keycard authorization for keycardId: {}, requestId: {}", call.getKeycardId(), requestId);
    try {
      Keycard keycard = getKeycard(call, requestId);
      boolean hasUnlimitedPrivileges = PrivilegeLevels.UNLIMITED.equals(keycard.getClearanceLevel().getLevel());
      boolean hasStandardAccess = PrivilegeLevels.STANDARD.equals(keycard.getClearanceLevel().getLevel());
      boolean hasClearance = !hasUnlimitedPrivileges && !hasStandardAccess &&
        call.getDestinationFloor().equals(keycard.getClearanceLevel().getFloor().getNumber());

      if (hasClearance || hasUnlimitedPrivileges) {
        return grantAccess(call, restrictedAccessGranted, requestId);
      } else {
        return denyAccess(call, accessDeniedRequests, requestId);
      }
    } catch (MissingDataException ex) {
      log.error("Keycard not found for keycardId: {}, requestId: {}", call.getKeycardId(), requestId);
      return denyAccess(call, accessDeniedRequests, requestId);
    }
  }

  private boolean denyAccess(ElevatorCallDto call, List<ElevatorCallDto> accessDeniedRequests, String requestId) {
    log.info("Access denied for elevator call with currentFloor: {}, destinationFloor: {} with keycardId: {}, requestId: {}",
      call.getCurrentFloor(), call.getDestinationFloor(), call.getKeycardId(), requestId);
    accessDeniedRequests.add(call);
    return false;
  }

  private boolean grantAccess(ElevatorCallDto call, List<ElevatorCallDto> restrictedAccessGranted, String requestId) {
    restrictedAccessGranted.add(call);
    return true;
  }

  private Keycard getKeycard(ElevatorCallDto call,  String requestId) {
    return keycardRepository.findById(call.getKeycardId())
      .orElseThrow(() -> new MissingDataException("No keycard found for keycardId: " + call.getKeycardId() + " requestId: " + requestId));
  }

  private Person getPerson(ElevatorCallDto call, String requestId) {
    return personRepository.findById(call.getPersonId())
      .orElseThrow(() -> new MissingDataException("No person found for personId: " + call.getPersonId() + " requestId: " + requestId));
  }
}
