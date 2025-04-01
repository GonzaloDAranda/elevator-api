package com.core.elevator_api.service;

import com.core.elevator_api.api.model.ClearanceLevel;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class KeycardAuthServiceTest {

  @MockitoBean
  private KeycardRepository keycardRepository;

  @MockitoBean
  private FloorRepository floorRepository;

  @MockitoBean
  private PersonRepository personRepository;

  @Autowired
  private KeycardAuthService keycardAuthService;

  private List<ElevatorCallDto> elevatorCalls;

  private List<ElevatorCallDto> accessDeniedRequests;

  private List<ElevatorCallDto> restrictedAccessGranted;

  private final String requestId = UUID.randomUUID().toString();
  private final String standardKeycardID = UUID.randomUUID().toString();
  private final String basementKeycardId = UUID.randomUUID().toString();
  private final String topFloorKeycardId = UUID.randomUUID().toString();
  private final String unlimitedKeycardId = UUID.randomUUID().toString();
  private final String person1Id = UUID.randomUUID().toString();
  private final String person2Id = UUID.randomUUID().toString();
  private final String person3Id = UUID.randomUUID().toString();
  private final String person4Id = UUID.randomUUID().toString();

  @BeforeEach
  void setup() {
    elevatorCalls = new ArrayList<>();
    accessDeniedRequests = new ArrayList<>();
    restrictedAccessGranted = new ArrayList<>();
  }

  @Test
  void getAuthorizedPublicElevatorCallsSuccess() {
    this.elevatorCalls = getElevatorCalls();
    elevatorCalls.get(0).setKeycardId(standardKeycardID);
    elevatorCalls.get(1).setKeycardId(basementKeycardId);
    elevatorCalls.get(2).setKeycardId(topFloorKeycardId);
    elevatorCalls.get(3).setKeycardId(unlimitedKeycardId);
    List<Floor> restrictedFloors = getFloors();
    when(this.floorRepository.findByAccessLevel(any(AccessLevels.class))).thenReturn(restrictedFloors);
    when(this.keycardRepository.findById(standardKeycardID)).thenReturn(Optional.of(this.geyKeycardById(standardKeycardID)));
    when(this.keycardRepository.findById(basementKeycardId)).thenReturn(Optional.of(this.geyKeycardById(basementKeycardId)));
    when(this.keycardRepository.findById(topFloorKeycardId)).thenReturn(Optional.of(this.geyKeycardById(topFloorKeycardId)));
    when(this.keycardRepository.findById(unlimitedKeycardId)).thenReturn(Optional.of(this.geyKeycardById(unlimitedKeycardId)));
    List<ElevatorCallDto> authorizedCalls =
      this.keycardAuthService.getAuthorizedPublicElevatorCalls(this.elevatorCalls, this.accessDeniedRequests,
        this.restrictedAccessGranted, requestId);
    assertNotNull(authorizedCalls);
    assertFalse(authorizedCalls.isEmpty());
    assertEquals(4, authorizedCalls.size());
    assertEquals(1, this.accessDeniedRequests.size());
    assertEquals(3, this.restrictedAccessGranted.size());
    assertEquals(elevatorCalls.get(4).getKeycardId(), this.accessDeniedRequests.get(0).getKeycardId());
    assertEquals(elevatorCalls.get(1).getKeycardId(), this.restrictedAccessGranted.get(0).getKeycardId());
    assertEquals(elevatorCalls.get(2).getKeycardId(), this.restrictedAccessGranted.get(1).getKeycardId());
    assertEquals(elevatorCalls.get(3).getKeycardId(), this.restrictedAccessGranted.get(2).getKeycardId());
  }

  @Test
  void getAuthorizedfreightElevatorCallsSuccess() {
    this.elevatorCalls = getElevatorCalls();
    elevatorCalls.get(0).setPersonId(person1Id);
    elevatorCalls.get(1).setPersonId(person2Id);
    elevatorCalls.get(2).setPersonId(person3Id);
    elevatorCalls.get(3).setPersonId(person4Id);
    List<Floor> restrictedFloors = getFloors();
    when(this.floorRepository.findByAccessLevel(any(AccessLevels.class))).thenReturn(restrictedFloors);
    when(this.personRepository.findById(person1Id)).thenReturn(Optional.of(this.getPersonById(person1Id)));
    when(this.personRepository.findById(person2Id)).thenReturn(Optional.of(this.getPersonById(person2Id)));
    when(this.personRepository.findById(person3Id)).thenReturn(Optional.of(this.getPersonById(person3Id)));
    when(this.personRepository.findById(person4Id)).thenReturn(Optional.of(this.getPersonById(person4Id)));
    List<ElevatorCallDto> authorizedCalls =
      this.keycardAuthService.getAuthorizedfreightElevatorCalls(this.elevatorCalls, this.accessDeniedRequests,
        this.restrictedAccessGranted, requestId);
    assertNotNull(authorizedCalls);
    assertFalse(authorizedCalls.isEmpty());
    assertEquals(2, authorizedCalls.size());
    assertEquals(3, this.accessDeniedRequests.size());
    assertEquals(2, this.restrictedAccessGranted.size());
    assertEquals(elevatorCalls.get(0).getPersonId(), this.accessDeniedRequests.get(0).getPersonId());
    assertEquals(elevatorCalls.get(1).getKeycardId(), this.accessDeniedRequests.get(1).getKeycardId());
    assertEquals(elevatorCalls.get(4).getKeycardId(), this.accessDeniedRequests.get(2).getKeycardId());
    assertEquals(elevatorCalls.get(2).getKeycardId(), this.restrictedAccessGranted.get(0).getKeycardId());
    assertEquals(elevatorCalls.get(3).getKeycardId(), this.restrictedAccessGranted.get(1).getKeycardId());
  }

  private List<Floor> getFloors() {
    Floor normalFloor = Floor.builder()
      .id(UUID.randomUUID().toString()).accessLevel(AccessLevels.PUBLIC).number(0).build();
    Floor basement = Floor.builder()
      .id(UUID.randomUUID().toString()).accessLevel(AccessLevels.RESTRICTED).number(-1).build();
    Floor topFloor = Floor.builder()
      .id(UUID.randomUUID().toString()).accessLevel(AccessLevels.RESTRICTED).number(49).build();
    return List.of(normalFloor, basement, topFloor);
  }

  private List<ElevatorCallDto> getElevatorCalls() {
    ElevatorCallDto call1 = ElevatorCallDto.builder().keycardId(standardKeycardID)
      .currentFloor(0).destinationFloor(1).build();
    ElevatorCallDto call2 = ElevatorCallDto.builder().keycardId(basementKeycardId)
      .currentFloor(0).destinationFloor(-1).build();
    ElevatorCallDto call3 = ElevatorCallDto.builder().keycardId(topFloorKeycardId)
      .currentFloor(30).destinationFloor(49).build();
    ElevatorCallDto call4 = ElevatorCallDto.builder().keycardId(unlimitedKeycardId)
      .currentFloor(40).destinationFloor(-1).build();
    ElevatorCallDto call5 = ElevatorCallDto.builder().currentFloor(10).destinationFloor(49).build();
    return List.of(call1, call2, call3, call4, call5);
  }

  private Keycard geyKeycardById(String keycardId) {
    return getKeycards().stream().filter(k -> keycardId.equals(k.getId())).findFirst().orElseThrow();
  }

  private List<Keycard> getKeycards() {
    Keycard keycard1 = Keycard.builder()
      .id(standardKeycardID)
      .clearanceLevel(ClearanceLevel.builder()
        .level(PrivilegeLevels.STANDARD)
        .build()).build();
    Keycard keycard2 = Keycard.builder()
      .id(basementKeycardId)
      .clearanceLevel(ClearanceLevel.builder()
        .level(PrivilegeLevels.BASEMENT)
        .floor(Floor.builder().number(-1).build())
        .build()).build();
    Keycard keycard3 = Keycard.builder()
      .id(topFloorKeycardId)
      .clearanceLevel(ClearanceLevel.builder()
        .level(PrivilegeLevels.TOP_FLOOR)
        .floor(Floor.builder().number(49).build())
        .build()).build();
    Keycard keycard4 = Keycard.builder()
      .id(unlimitedKeycardId)
      .clearanceLevel(ClearanceLevel.builder()
        .level(PrivilegeLevels.UNLIMITED)
        .build()).build();
    return List.of(keycard1, keycard2, keycard3, keycard4);
  }

  private Person getPersonById(String personId) {
    return getPersons().stream().filter(p -> personId.equals(p.getId())).findFirst().orElseThrow();
  }

  private List<Person> getPersons() {
    Person person1 = Person.builder().id(person1Id).role(Roles.TENANT.name()).build();
    Person person2 = Person.builder().id(person2Id).role(Roles.TENANT.name()).build();
    Person person3 = Person.builder().id(person3Id).role(Roles.TECHNICIAN.name()).build();
    Person person4 = Person.builder().id(person4Id).role(Roles.MANAGER.name()).build();
    return List.of(person1, person2, person3, person4);
  }

}
