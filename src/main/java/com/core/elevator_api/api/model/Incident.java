package com.core.elevator_api.api.model;

import com.core.elevator_api.constants.IncidentTypes;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "incident")
public class Incident {

  @Id
  @NotNull
  private String id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private IncidentTypes type;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = Elevator.class)
  @JoinColumn(name = "elevator_id")
  private Elevator elevator;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = Floor.class)
  @JoinColumn(name = "floor_id")
  private Floor floor;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy HH:mm:ss")
  private LocalDateTime time;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy HH:mm:ss")
  private LocalDateTime  lastUpdatedAt;

}
