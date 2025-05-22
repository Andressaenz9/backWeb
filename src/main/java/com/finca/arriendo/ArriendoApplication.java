package com.finca.arriendo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArriendoApplication {

	public static void main(String[] args) {
		System.out.println("JWT_SECRET env: " + System.getenv("JWT_SECRET"));
		SpringApplication.run(ArriendoApplication.class, args);
	}

}
