package com.project.ipms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IpmServiceTests {
    /**
     * File Controller.
     */
    @Autowired
    private IpmService mainService;

    @Test
    void serviceStatusTest() {
        String response = mainService.welcome();
        assertEquals(response, "Welcome to IPMS!");
    }
}
