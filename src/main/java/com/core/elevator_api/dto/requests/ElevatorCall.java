package com.core.elevator_api.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElevatorCall {

  @NotNull(message = "The field weight can't be empty")
  private Integer weight;

  private String keycardId;

  private String personId;

  @Range(min=-1, max=49 , message = "The field currentFloor is invalid. It must be between -1 and 49")
  private Integer currentFloor;

  @Range(min=-1, max=49 , message = "The field destinationFloor is invalid. It must be between -1 and 49")
  private Integer destinationFloor;

}
