package com.example.classroom_reservation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ClassroomReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassroomReservationSystemApplication.class, args);
	}

}
