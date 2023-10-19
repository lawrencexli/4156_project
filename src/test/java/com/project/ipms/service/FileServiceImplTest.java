package com.project.ipms.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.project.ipms.exception.CriticalServerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
class FileServiceImplTest {

    /**
     * Fake Bucket Name.
     */
    private String fakeBucketName;

    /**
     * Fake Google Cloud Storage object.
     */
    @Mock
    private Storage fakeStorage;

    /**
     * FileService for unit test.
     */
    @InjectMocks
    private FileServiceImpl fileService;

    /**
     * Test image file.
     */
    private byte[] dummyImageByte;

    @BeforeEach
    void setUp() {
        try {
            String testBucketName = "test-bucket-name";
            fileService = new FileServiceImpl(fakeStorage);
            ReflectionTestUtils.setField(fileService, "bucketName", testBucketName);
            fakeBucketName = testBucketName;
            BufferedImage dummyImageBuffer = ImageIO.read(
                    ResourceUtils.getFile("src/test/resources/test1.jpg")
            );
            ByteArrayOutputStream dummyImageStream = new ByteArrayOutputStream();
            ImageIO.write(dummyImageBuffer, "jpg", dummyImageStream);
            dummyImageByte = dummyImageStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("File service test set up failed: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        fileService = null;
        fakeBucketName = null;
    }

    @Test
    void testDownloadFile1() {
        String testFileName = "maya-fey.jpg";
        Blob mockedBlob = mock(Blob.class);
        Mockito.when(fakeStorage.get(fakeBucketName, testFileName)).
                thenReturn(mockedBlob);

        Mockito.when(mockedBlob.getContent()).
                thenReturn(dummyImageByte);

        assertDoesNotThrow(() -> fileService.downloadFile(testFileName));
    }

    @Test
    void testDownloadFile2() {
        String testFileName = "maya-fey.jpg";
        Mockito.when(fakeStorage.get(fakeBucketName, testFileName)).
                thenReturn(null);

        Exception exception = assertThrows(CriticalServerException.class, () ->
                fileService.downloadFile(testFileName));

        String expectedMessage = "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUploadFile() {
        String testRepoName = "phoenix-wright-1011";
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "test",
                "test.jpg",
                "image/jpg",
                dummyImageByte
        );

        assertDoesNotThrow(() ->
                fileService.uploadFile(
                        testMultipartFile.getOriginalFilename(),
                        testMultipartFile.getContentType(),
                        testMultipartFile.getBytes(),
                        testRepoName)
        );
    }
}
