package com.core.elevator_api.mapper;

import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.dto.ElevatorDto;
import com.core.elevator_api.dto.requests.ElevatorCall;
import com.core.elevator_api.dto.ElevatorCallDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ElevatorMapper {

  @BeanMapping(nullValuePropertyMappingStrategy =
    NullValuePropertyMappingStrategy.IGNORE)
  List<ElevatorCallDto> mapElevatorCalls(List<ElevatorCall> elevatorCalls);

  @BeanMapping(nullValuePropertyMappingStrategy =
    NullValuePropertyMappingStrategy.IGNORE)
  ElevatorDto mapElevatorToDto(Elevator elevator);

  @BeanMapping(nullValuePropertyMappingStrategy =
    NullValuePropertyMappingStrategy.IGNORE)
  Elevator mapElevatorDtoToEntity(ElevatorDto elevator);

}
