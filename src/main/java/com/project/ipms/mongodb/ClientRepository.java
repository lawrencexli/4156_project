package com.project.ipms.mongodb;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<ClientEntry, String> {
    /**
     * Get client entry by client ID token.
     * Method used by MongoDB to query entries.
     * @param id client ID.
     * @return ImageFileEntry class.
     */
    ClientEntry getClientEntryById(String id);

    /**
     * Delete client entry by client ID token.
     * Method used by MongoDB to query entries.
     * Only for integration testing purposes.
     * @param id client ID.
     */
    @DeleteQuery
    void deleteClientEntryById(String id);
}
