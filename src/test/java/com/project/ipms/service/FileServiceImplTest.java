package com.project.ipms.service;

import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootTest
class FileServiceImplTest {

    /**
     * Fake Bucket Name
     */
    String fakeBucketName;

    /**
     * Fake Google Cloud Storage object.
     */
    @Mock
    Storage fakeStorage;

    /**
     * FileService for unit test.
     */
    @InjectMocks
    FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl(fakeStorage);
        fakeBucketName = "ace-attorney-7";
    }

    @AfterEach
    void tearDown() {
        fileService = null;
        fakeBucketName = null;
    }

    @Test
    void downloadFile() throws SQLException {
    }

    @Test
    void uploadFile() throws IOException {
    }
}