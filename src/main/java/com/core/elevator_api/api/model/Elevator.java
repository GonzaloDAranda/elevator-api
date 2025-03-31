package com.core.elevator_api.api.model;

import com.core.elevator_api.constants.ElevatorStatus;
import com.core.elevator_api.constants.ElevatorTypes;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "elevator")
public class Elevator {

  @Id
  @NotNull
  private String id;

  @Enumerated(EnumType.STRING)
  private ElevatorTypes type;

  @Enumerated(EnumType.STRING)
  private ElevatorStatus status;

  @NotNull
  private Integer currentFloor;

  @NotNull
  private Integer maxSupportedWeight;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy HH:mm:ss")
  private LocalDateTime  lastUpdatedAt;

}
