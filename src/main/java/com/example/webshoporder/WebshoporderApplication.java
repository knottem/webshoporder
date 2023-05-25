package com.example.webshoporder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
public class WebshoporderApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebshoporderApplication.class, args);
	}

}
