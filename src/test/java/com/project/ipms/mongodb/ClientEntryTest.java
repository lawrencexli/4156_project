package com.project.ipms.mongodb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ClientEntryTest {
    @Test
    void clientEntryTest1() {
        ClientEntry testClientEntry1 = new ClientEntry(null, new HashSet<>());
        assertNull(testClientEntry1.getId());
        assertTrue(testClientEntry1.getImageFileList().isEmpty());
    }

    @Test
    void clientEntryTest2() {
        ClientEntry testClientEntry2 = new ClientEntry(null, new HashSet<>());
        assertNull(testClientEntry2.getId());
        assertTrue(testClientEntry2.getImageFileList().isEmpty());

        String testFileName = "not_exist.jpg";
        assertFalse(testClientEntry2.fileNameInImageFileList(testFileName));

        testClientEntry2.addToImageFileList("fake1.jpg");
        testClientEntry2.addToImageFileList("fake2.png");
        testClientEntry2.addToImageFileList("fake3.jpeg");
        assertEquals(testClientEntry2.getImageFileList().size(), 3);

        String testFileName2 = "fake3.jpeg";
        assertTrue(testClientEntry2.fileNameInImageFileList(testFileName2));
    }
}
