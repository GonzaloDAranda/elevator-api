package com.core.elevator_api.dto;

import com.core.elevator_api.constants.Directions;
import com.core.elevator_api.constants.ElevatorStatus;
import com.core.elevator_api.constants.ElevatorTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorDto {

  private String id;


  private ElevatorTypes type;

  private ElevatorStatus status;

  private Integer currentFloor;

  private Integer nextFloor;

  private Integer maxSupportedWeight;

  private Integer currentWeight;

  private Directions direction;

  private LocalDateTime createdAt;

  private LocalDateTime  lastUpdatedAt;

}
