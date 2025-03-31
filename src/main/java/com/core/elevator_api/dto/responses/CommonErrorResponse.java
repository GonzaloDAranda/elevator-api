package com.core.elevator_api.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonErrorResponse {

  private List<String> errorMessages;

  private String status;

  private int statusCode;
}
