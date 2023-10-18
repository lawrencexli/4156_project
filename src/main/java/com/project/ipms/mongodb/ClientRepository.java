package com.project.ipms.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<ClientEntry, String> {
    /**
     * Get client entry by client ID token.
     * Method used by MongoDB to query entries.
     * @param id Image ID.
     * @return ImageFileEntry class.
     */
    ClientEntry getClientEntryById(String id);
}
