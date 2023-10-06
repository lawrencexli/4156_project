package com.project.ipms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class IpmService {
	/**
	 * Run the service.
	 * @param args Default arguments
	 */
	public static void main(final String[] args) {
		SpringApplication.run(IpmService.class, args);
    }

	/**
	 * API call for index page.
	 * @param name The name to be displayed
	 * @return A string response
	 */
	@GetMapping("/")
	public String hello(final @RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
}
