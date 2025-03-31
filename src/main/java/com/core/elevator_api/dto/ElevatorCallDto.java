package com.core.elevator_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElevatorCallDto {

  private Integer weight;

  private String keycardId;

  private String personId;

  private Integer currentFloor;

  private Integer destinationFloor;

}
