package com.example.hostelmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HostelmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostelmanagementApplication.class, args);
	}

}
