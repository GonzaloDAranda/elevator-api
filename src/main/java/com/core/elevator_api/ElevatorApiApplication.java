package com.core.elevator_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.core.elevator_api"})
public class ElevatorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElevatorApiApplication.class, args);
	}

}
