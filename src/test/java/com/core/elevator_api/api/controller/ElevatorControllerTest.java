package com.core.elevator_api.api.controller;

import com.core.elevator_api.TestUtils;
import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.constants.ElevatorTypes;
import com.core.elevator_api.dto.responses.ElevatorInfoResponse;
import com.core.elevator_api.mapper.ElevatorMapper;
import com.core.elevator_api.service.ElevatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class ElevatorControllerTest {

  private static final String CALL_PUBLIC_ELEVATOR_PATH = "/callPublicElevator";

  private static final String CALL_FREIGHT_ELEVATOR_PATH = "/callFreightElevator";

  private static final String GET_ELEVATORS_INFO_PATH = "/getElevatorsInfo";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ElevatorService elevatorService;

  @Autowired
  private ElevatorMapper elevatorMapper;

  @Test
  void callPublicElevatorSuccess() throws Exception {
    String request = TestUtils.getContentFromJsonFile("requests/CallPublicElevatorOkRequest.json");
    this.mockMvc.perform(
      MockMvcRequestBuilders.post(CALL_PUBLIC_ELEVATOR_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(request)
        .param("requestId", UUID.randomUUID().toString())
    ).andExpect(status().isOk());

    verify(this.elevatorService, times(1))
      .callPublicElevator(anyList(), anyString());
  }

  @Test
  void callFreightElevatorSuccess() throws Exception {
    String request = TestUtils.getContentFromJsonFile("requests/CallFreightElevatorOkRequest.json");
    this.mockMvc.perform(
      MockMvcRequestBuilders.post(CALL_FREIGHT_ELEVATOR_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(request)
        .param("requestId", UUID.randomUUID().toString())
    ).andExpect(status().isOk());

    verify(this.elevatorService, times(1))
      .callFreightElevator(anyList(), anyString());
  }

  @Test
  void getGetElevatorInfoNoContent() throws Exception {
    this.mockMvc.perform(
      MockMvcRequestBuilders.get(GET_ELEVATORS_INFO_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("requestId", UUID.randomUUID().toString())
    ).andExpect(status().isNoContent());
  }

  @Test
  void getGetElevatorInfoSuccess() throws Exception {
    when(this.elevatorService.getElevatorsInfo(anyString(), any(ElevatorTypes.class), anyString()))
      .thenReturn(ElevatorInfoResponse.builder().elevators(List.of(Elevator.builder().build())).build());
    this.mockMvc.perform(
      MockMvcRequestBuilders.get(GET_ELEVATORS_INFO_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("id", UUID.randomUUID().toString())
        .param("type", "PUBLIC")
        .param("requestId", UUID.randomUUID().toString())
    ).andExpect(status().isOk());

    verify(this.elevatorService, times(1))
      .getElevatorsInfo(anyString(), any(ElevatorTypes.class), anyString());
  }
}
