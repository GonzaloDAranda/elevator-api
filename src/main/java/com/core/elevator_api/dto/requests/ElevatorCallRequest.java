package com.core.elevator_api.dto.requests;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ElevatorCallRequest {

  private List<@Valid ElevatorCall> elevatorCalls;

}
