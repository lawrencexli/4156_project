package com.project.ipms.mongodb;

import com.project.ipms.exception.FileAlreadyExistsException;
import com.project.ipms.exception.FileNotFoundException;
import com.project.ipms.exception.InvalidCredentialsException;
import com.project.ipms.util.ImageFileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MongoDBServiceImplTest {

    /**
     * Mock MongoDB client repository.
     */
    @Mock
    private ClientRepository fakeIpmsMongoRepo;

    /**
     * The class for unit test.
     */
    @InjectMocks
    private MongoDBServiceImpl mongoDBService;

    @BeforeEach
    void setUp() {
        mongoDBService = new MongoDBServiceImpl(fakeIpmsMongoRepo);
    }

    @AfterEach
    void tearDown() {
        mongoDBService = null;
    }

    @Test
    void testGenerateNewKey() {
        mongoDBService.generateNewKey();
    }

    @Test
    void testMongoDBFileCheck1() {
        String testID = "phoenix-wright-1011";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String fileName = "non-existent.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        Exception exception = assertThrows(FileNotFoundException.class, () ->
                mongoDBService.mongoDBFileCheck(testID, fileName));

        String expectedMessage = "File does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMongoDBFileCheck2() {
        String testID = "invalid-id-404";
        String fileName = "test1.jpg";

        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(null);

        Exception exception = assertThrows(InvalidCredentialsException.class, () ->
                mongoDBService.mongoDBFileCheck(testID, fileName));

        String expectedMessage = "Invalid Client ID";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMongoDBFileCheck3() {
        String testID = "maya-fey";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String fileName = "test1.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        assertDoesNotThrow(() -> mongoDBService.mongoDBFileCheck(testID, fileName));
    }

    @Test
    void testUploadFile1() {
        String testID = "invalid-id-404";
        String fileName = "test1.jpg";

        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(null);

        // Mock ImageFileUtil.checkFileValid() static method from utility class
        try (MockedStatic<ImageFileUtil> utilities = Mockito.mockStatic(ImageFileUtil.class)) {
            utilities.when(() -> ImageFileUtil.checkFileValid(fileName)).
                    thenReturn(".jpg");

            Exception exception = assertThrows(InvalidCredentialsException.class, () ->
                    mongoDBService.uploadFile(testID, fileName));

            String expectedMessage = "Invalid Client ID";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Test
    void testUploadFile2() {
        String testID = "phoenix-wright-1011";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String fileName = "test1.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        // Mock ImageFileUtil.checkFileValid() static method from utility class
        try (MockedStatic<ImageFileUtil> utilities = Mockito.mockStatic(ImageFileUtil.class)) {
            utilities.when(() -> ImageFileUtil.checkFileValid(fileName)).
                    thenReturn(".jpg");

            Exception exception = assertThrows(FileAlreadyExistsException.class, () ->
                    mongoDBService.uploadFile(testID, fileName));

            String expectedMessage = "Filename already exists";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Test
    void testUploadFile3() {
        String testID = "phoenix-wright-1011";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String fileName = "test4.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        // Mock ImageFileUtil.checkFileValid() static method from utility class
        try (MockedStatic<ImageFileUtil> utilities = Mockito.mockStatic(ImageFileUtil.class)) {
            utilities.when(() -> ImageFileUtil.checkFileValid(fileName)).
                    thenReturn(".jpg");

            assertDoesNotThrow(() -> mongoDBService.uploadFile(testID, fileName));
        }
    }

    @Test
    void testMongoDBOperationCheck1() {
        String testID = "maya-fey";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String targetFileName = "test1.jpg";
        String resultFileName = "test1-result.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        assertDoesNotThrow(() -> mongoDBService.mongoDBOperationCheck(testID, targetFileName, resultFileName));
    }

    @Test
    void testMongoDBOperationCheck2() {
        String testID = "maya-fey";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String targetFileName = "test4.jpg";
        String resultFileName = "test4-result.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        Exception exception = assertThrows(FileNotFoundException.class, () ->
                mongoDBService.mongoDBOperationCheck(testID, targetFileName, resultFileName));

        String expectedMessage = "Target file does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMongoDBOperationCheck3() {
        String testID = "maya-fey";
        List<String> testImageFileList = new ArrayList<>();
        testImageFileList.add("test1.jpg");
        testImageFileList.add("test2.jpg");
        testImageFileList.add("test3.jpg");

        String targetFileName = "test3.jpg";
        String resultFileName = "test2.jpg";
        Mockito.when(fakeIpmsMongoRepo.getClientEntryById(testID)).
                thenReturn(new ClientEntry(testID, new HashSet<>(testImageFileList)));

        Exception exception = assertThrows(FileAlreadyExistsException.class, () ->
                mongoDBService.mongoDBOperationCheck(testID, targetFileName, resultFileName));

        String expectedMessage = "Result filename already exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
