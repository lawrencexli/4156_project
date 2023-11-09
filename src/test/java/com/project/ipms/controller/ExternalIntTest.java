package com.project.ipms.controller;

import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class ExternalIntTest {

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
            fileController.imageTransparent("objection.png", "objection2.png", clientID, 0.5F);
            fileController.imageCrop("objection2.png", "objection3.png", clientID, 300, 300, 600, 600);
            fileController.imageSaturate("objection3.png", "objection4.png", clientID, 0.5F);
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
            throw new RuntimeException("File controller integration test 1 failed: " + e.getMessage());
        }
    }
}
