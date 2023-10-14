package com.project.ipms.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;

@SpringBootTest
class FileServiceImplTest {

    /**
     * Fake Bucket Name
     */
    @Value("${gcp.bucket.name}")
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
    }

    @AfterEach
    void tearDown() {
        fileService = null;
    }

    @Test
    void downloadFile() {
        String testFileName = "maya-fey.jpg";
        Blob mockedBlob = mock(Blob.class);
        Mockito.when(fakeStorage.get(fakeBucketName, testFileName)).
                thenReturn(mockedBlob);

        fileService.downloadFile(testFileName);
    }

    @Test
    void uploadFile() {
    }
}