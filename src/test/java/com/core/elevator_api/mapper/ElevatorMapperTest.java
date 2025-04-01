package com.core.elevator_api.mapper;

import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.dto.ElevatorCallDto;
import com.core.elevator_api.dto.ElevatorDto;
import com.core.elevator_api.dto.requests.ElevatorCall;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ElevatorMapperTest {

  @Autowired
  private ElevatorMapper elevatorMapper;

  @Test
  void mapElevatorCallRequestTest() {
    ElevatorCall elevatorCall = ElevatorCall.builder().weight(50).currentFloor(0).destinationFloor(1).build();
    List<ElevatorCallDto> elevatorCallDtos = elevatorMapper.mapElevatorCalls(List.of(elevatorCall));
    ElevatorCallDto elevatorCallDto = elevatorCallDtos.get(0);
    assertEquals(elevatorCall.getWeight(),elevatorCallDto.getWeight());
    assertEquals(elevatorCall.getCurrentFloor(),elevatorCallDto.getCurrentFloor());
    assertEquals(elevatorCall.getDestinationFloor(),elevatorCallDto.getDestinationFloor());
  }

  @Test
  void mapElevatorToDtoTest() {
    Elevator elevator = Elevator.builder()
      .id(UUID.randomUUID().toString()).type(ElevatorTypes.PUBLIC).maxSupportedWeight(1000).build();
    ElevatorDto elevatorDto = elevatorMapper.mapElevatorToDto(elevator);
    assertEquals(elevator.getId(),elevatorDto.getId());
    assertEquals(elevator.getCurrentFloor(),elevatorDto.getCurrentFloor());
    assertEquals(elevator.getType(),elevatorDto.getType());
  }

  @Test
  void mapElevatorDtoToEntityTest() {
    ElevatorDto elevatorDto = ElevatorDto.builder()
      .id(UUID.randomUUID().toString()).type(ElevatorTypes.PUBLIC).maxSupportedWeight(1000).build();
    Elevator elevator = elevatorMapper.mapElevatorDtoToEntity(elevatorDto);
    assertEquals(elevator.getId(),elevatorDto.getId());
    assertEquals(elevator.getCurrentFloor(),elevatorDto.getCurrentFloor());
    assertEquals(elevator.getType(),elevatorDto.getType());
  }
}
