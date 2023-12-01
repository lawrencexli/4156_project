package com.project.ipms;

import com.project.ipms.controller.FileController;
import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.FileNotFoundException;
import com.project.ipms.exception.InvalidCredentialsException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * External and internal integration test for testing MongoDB, Google Cloud Bucket,
 * And all service API endpoints and components.
 * Also test for concurrent client instances (Each test simulates an individual client instance).
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public class IntegrationFullTest {

    /**
     * Real file service.
     */
    @Autowired
    private FileService fileService;

    /**
     * Real MongoDB service interface.
     */
    @Autowired
    private MongoDBService mongoDBService;

    /**
     * Real Image file service interface.
     */
    @Autowired
    private ImageService imageFileService;

    /**
     * Real controller.
     */
    private FileController fileController;

    /**
     * Temp client ID.
     */
    private String clientID;

    /**
     * Setup file controller and generate a new client ID for integration test.
     */
    @BeforeEach
    void setUp() {
        fileController = new FileController(fileService, mongoDBService, imageFileService);
        clientID = fileController.generateClientID().getResponseMessage();
    }

    /**
     * Delete the test client ID after test.
     */
    @AfterEach
    void tearDown() {
        List<String> list = fileService.listOfFiles(clientID);
        for (String file : list) {
            fileService.deleteFile(file);
        }
        mongoDBService.deleteClientEntry(clientID);
        clientID = null;
        fileController = null;
    }

    @Test
    void fileControllerIntTest1() {
        String originalPath = "src/test/resources/apple.png";
        String resultPath = "src/test/resources/apple_int_result_1.png";


        try (InputStream testInputStream = new FileInputStream(originalPath)) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "apple",
                    "apple.png",
                    "image/png",
                    testInputStream
            );

            fileController.uploadFile(testMultipartFile, clientID);
            ResponseEntity<Resource> result =  fileController.downloadFile("apple.png", clientID);
            InputStream resource = Objects.requireNonNull(result.getBody()).getInputStream();
            BufferedImage imgResult = ImageIO.read(resource);

            File out = new File(resultPath);
            ImageIO.write(imgResult, "png", out);

            File resultFile = ResourceUtils.getFile(resultPath);
            File trueResultFile = ResourceUtils.getFile(originalPath);

            BufferedImage testResultPng = ImageIO.read(resultFile);
            BufferedImage testTrueResultPng = ImageIO.read(trueResultFile);

            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultPng, testTrueResultPng));
        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 1 failed: " + e.getMessage());
        }
    }

    @Test
    void fileControllerIntTest2() {
        String originalPath = "src/test/resources/objection.png";
        String resultPath = "src/test/resources/objection_test_result.png";
        String trueResultPath = "src/test/resources/objection_result.png";

        try (InputStream testInputStream = new FileInputStream(originalPath)) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "objection",
                    "objection.png",
                    "image/png",
                    testInputStream
            );

            fileController.uploadFile(testMultipartFile, clientID);
            fileController.imageTransparent("objection.png", "objection2.png", clientID, "0.5");
            fileController.imageCrop("objection2.png", "objection3.png", clientID, "300", "300", "600", "600");
            fileController.imageSaturate("objection3.png", "objection4.png", clientID, "0.5");
            ResponseEntity<Resource> result =  fileController.downloadFile("objection4.png", clientID);

            InputStream resource = Objects.requireNonNull(result.getBody()).getInputStream();
            BufferedImage imgResult = ImageIO.read(resource);

            File out = new File(resultPath);
            ImageIO.write(imgResult, "png", out);

            File resultFile = ResourceUtils.getFile(resultPath);
            File trueResultFile = ResourceUtils.getFile(trueResultPath);

            BufferedImage testResultPng = ImageIO.read(resultFile);
            BufferedImage testTrueResultPng = ImageIO.read(trueResultFile);

            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultPng, testTrueResultPng));
        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 2 failed: " + e.getMessage());
        }
    }

    @Test
    void fileControllerIntTest3() {
        String originalPath1 = "src/test/resources/ace-attorney-cover-art.jpg";
        String originalPath2 = "src/test/resources/test1.jpg";
        String resultPath = "src/test/resources/integration_test_result.png";
        String trueResultPath = "src/test/resources/integration_true_result.png";

        try (InputStream testInputStream1 = new FileInputStream(originalPath1);
             InputStream testInputStream2 = new FileInputStream(originalPath2)) {
            MockMultipartFile testMultipartFile1 = new MockMultipartFile(
                    "ace-attorney-cover-art",
                    "ace-attorney-cover-art.jpg",
                    "image/jpg",
                    testInputStream1
            );

            MockMultipartFile testMultipartFile2 = new MockMultipartFile(
                    "test1",
                    "test1.jpg",
                    "image/jpg",
                    testInputStream2
            );

            fileController.uploadFile(testMultipartFile1, clientID);
            fileController.imageTransparent("ace-attorney-cover-art.jpg",
                    "ace-attorney-cover-art2.jpg", clientID, "0.7");
            fileController.imageCrop("ace-attorney-cover-art2.jpg",
                    "ace-attorney-cover-art3.jpg", clientID, "150", "50", "400", "150");
            fileController.imageSaturate("ace-attorney-cover-art3.jpg",
                    "ace-attorney-cover-art4.jpg", clientID, "0.4");

            fileController.uploadFile(testMultipartFile2, clientID);
            fileController.imageSaturate("test1.jpg",
                    "test2.jpg", clientID, "0.35");
            fileController.imageCrop("test2.jpg",
                    "test3.jpg", clientID, "20", "20", "100", "100");
            fileController.imageTransparent("test3.jpg", "test4.jpg", clientID, "0.7");

            fileController.imageOverlay("ace-attorney-cover-art4.jpg",
                    "test4.jpg", "result.jpg", clientID, "0", "0");

            ResponseEntity<Resource> result = fileController.downloadFile("result.jpg", clientID);

            InputStream resource = Objects.requireNonNull(result.getBody()).getInputStream();
            BufferedImage imgResult = ImageIO.read(resource);

            File out = new File(resultPath);
            ImageIO.write(imgResult, "jpg", out);

            File resultFile = ResourceUtils.getFile(resultPath);
            File trueResultFile = ResourceUtils.getFile(trueResultPath);

            BufferedImage testResultPng = ImageIO.read(resultFile);
            BufferedImage testTrueResultPng = ImageIO.read(trueResultFile);

            Assertions.assertTrue(ImageFileUtil.compareImagesEqual(testResultPng, testTrueResultPng));
        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 3 failed: " + e.getMessage());
        }
    }

    @Test
    void fileControllerIntTest4() {
        String originalPath = "src/test/resources/apple.png";

        try (InputStream testInputStream = new FileInputStream(originalPath)) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "apple",
                    "apple.png",
                    "image/png",
                    testInputStream
            );

            Exception exception1 = assertThrows(InvalidCredentialsException.class,
                    () -> fileController.uploadFile(testMultipartFile, "invalid-id"));

            String expectedMessage1 = "Invalid Client ID";
            String actualMessage1 = exception1.getMessage();
            assertTrue(actualMessage1.contains(expectedMessage1));

            Exception exception2 = assertThrows(FileNotFoundException.class,
                    () ->  fileController.downloadFile("not-exist.png", clientID));

            String expectedMessage2 = "File does not exist";
            String actualMessage2 = exception2.getMessage();
            assertTrue(actualMessage2.contains(expectedMessage2));

        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 4 failed: " + e.getMessage());
        }
    }

    @Test
    void fileControllerIntTest5() {
        String originalPath = "src/test/resources/nope.pdf";

        try (InputStream testInputStream = new FileInputStream(originalPath)) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "nope",
                    "nope.pdf",
                    "image/jpg",
                    testInputStream
            );

            Exception exception1 = assertThrows(BadRequestException.class,
                    () -> fileController.uploadFile(testMultipartFile, clientID));

            String expectedMessage = "Image file validation failed: "
                    + "The file could be corrupted or is not an image file";
            String actualMessage = exception1.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 5 failed: " + e.getMessage());
        }
    }

    @Test
    void fileControllerIntTest6() {
        String originalPath = "src/test/resources/objection.png";

        try (InputStream testInputStream = new FileInputStream(originalPath)) {
            MockMultipartFile testMultipartFile = new MockMultipartFile(
                    "objection",
                    "objection.png",
                    "image/png",
                    testInputStream
            );

            fileController.uploadFile(testMultipartFile, clientID);

            Exception exception1 = assertThrows(NumberFormatException.class,
                    () -> fileController.imageTransparent("objection.png",
                            "objection2.png", clientID, "abc"));

            String expectedMessage1 = "For input string: \"abc\"";
            String actualMessage1 = exception1.getMessage();
            assertTrue(actualMessage1.contains(expectedMessage1));

            Exception exception2 = assertThrows(FileNotFoundException.class,
                    () -> fileController.imageCrop("objection2.png",
                            "objection3.png", clientID, "300", "300", "600", "600"));

            String expectedMessage2 = "Target file does not exist";
            String actualMessage2 = exception2.getMessage();
            assertTrue(actualMessage2.contains(expectedMessage2));
        } catch (IOException e) {
            throw new RuntimeException("File controller integration test 6 failed: " + e.getMessage());
        }
    }
}
