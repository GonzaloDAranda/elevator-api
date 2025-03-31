package com.core.elevator_api.dto.responses;

import com.core.elevator_api.api.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentsResponse {

  List<Incident> incidents;
}
