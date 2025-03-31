package com.core.elevator_api.dto.responses;

import com.core.elevator_api.dto.ElevatorCallDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElevatorCallResponse {

  private List<String> trajectory;

  private Integer currentFloor;

  private List<ElevatorCallDto> accessDeniedRequests;

  private List<ElevatorCallDto> restrictedAccessGranted;

}
