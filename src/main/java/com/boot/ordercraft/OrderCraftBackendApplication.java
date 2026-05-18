package com.boot.ordercraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderCraftBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderCraftBackendApplication.class, args);
	}

}
