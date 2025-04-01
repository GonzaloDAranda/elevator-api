package com.core.elevator_api;

import com.core.elevator_api.api.controller.ElevatorController;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElevatorApiApplicationTests {

	@Autowired
	private ElevatorController elevatorController;

	@Mock
	private ElevatorApiApplication elevatorApiApplication;

	@Test
	void contextLoads() {
		assertThat(elevatorController).isNotNull();
	}

}
