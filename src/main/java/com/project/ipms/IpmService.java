package com.project.ipms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableMongoRepositories
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
     * @return Welcome message
     */
    @GetMapping("/")
    public String welcome() {
        return "Welcome to IPMS!";
    }
}
