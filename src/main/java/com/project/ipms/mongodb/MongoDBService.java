package com.project.ipms.mongodb;

import java.io.IOException;

public interface MongoDBService {

    /**
     * Generate a new client ID as API key credentials.
     * @return a newly generated client ID
     */
    String generateNewKey();

    /**
     * Check if the client ID is in the database.
     * Then check if the fileName associated with the client ID exists
     * @param fileName Name of file
     * @param id client ID
     */
    void mongoDBFileCheck(String id, String fileName);

    /**
     * Post a filename record to associated client ID in MongoDB database.
     * @param id client ID
     * @param fileName fileName intended for upload
     */
    void uploadFile(String id, String fileName) throws IOException;
}
