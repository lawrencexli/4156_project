package com.project.ipms.mongodb;

import com.project.ipms.exception.FileAlreadyExistsException;
import com.project.ipms.exception.FileNotFoundException;
import com.project.ipms.exception.InvalidCredentialsException;
import com.project.ipms.util.ImageFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class MongoDBServiceImpl implements MongoDBService {

    /**
     * MongoDB repo service.
     */
    private final ClientRepository ipmsMongoRepo;

    /**
     * Implementation of the MongoDBService.
     * @param ipmsMongoRepo The MongoDB repository service
     */
    @Autowired
    public MongoDBServiceImpl(final ClientRepository ipmsMongoRepo) {
        this.ipmsMongoRepo = ipmsMongoRepo;
    }

    /**
     * Generate a new client ID as API key credentials.
     * @return a newly generated client ID
     */
    @Override
    public String generateNewKey() {
        ClientEntry newClientEntry = new ClientEntry(null, new HashSet<>());
        ipmsMongoRepo.insert(newClientEntry);
        return newClientEntry.getId();
    }

    /**
     * Check if the client ID is in the database.
     * Then check if the fileName associated with the client ID exists
     * @param fileName Name of file
     * @param id client ID
     */
    @Override
    public void mongoDBFileCheck(final String id, final String fileName) {
        ClientEntry clientEntry = ipmsMongoRepo.findClientEntryById(id);
        if (clientEntry == null) {
            throw new InvalidCredentialsException("Invalid Client ID");
        }
        if (!clientEntry.fileNameInImageFileList(fileName)) {
            throw new FileNotFoundException("File does not exist");
        }
    }

    /**
     * Post a filename record to associated client ID in MongoDB database.
     * @param id client ID
     * @param fileName fileName intended for upload
     */
    @Override
    public void uploadFile(final String id, final String fileName) {
        ClientEntry clientEntry = ipmsMongoRepo.findClientEntryById(id);
        if (clientEntry == null) {
            throw new InvalidCredentialsException("Invalid Client ID");
        }
        ImageFileUtil.checkFileValid(fileName);
        if (clientEntry.fileNameInImageFileList(fileName)) {
            throw new FileAlreadyExistsException("Filename already exists");
        }
        clientEntry.addToImageFileList(fileName);
        ipmsMongoRepo.save(clientEntry);
    }
}
