package com.project.ipms;

import com.project.ipms.controller.FileController;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IpmServiceTests {
	/**
	 * File Controller.
	 */
	@Autowired
	private FileController homeFileController;

	@Test
	void contextLoads() {
		assertThat(homeFileController).isNotNull();
	}
}
