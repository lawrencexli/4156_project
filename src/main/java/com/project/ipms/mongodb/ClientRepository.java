package com.project.ipms.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<ClientEntry, String> {
    /**
     * Find image entry by image ID.
     * @param id Image ID.
     * @return ImageFileEntry class.
     */
    ClientEntry findClientEntryById(String id);
}
