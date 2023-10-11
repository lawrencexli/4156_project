package com.project.ipms.mongodb;

import lombok.Getter;
import java.util.HashSet;

import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("client-entry")
public class ClientEntry {
    /**
     * Client ID.
     */
    @Id
    private String id;

    /**
     * List of image files associated with the client.
     */
    private HashSet<String> imageFileList;

    /**
     * Constructor.
     * @param id client id
     * @param imageFileList image file list
     */
    public ClientEntry(final String id,
                       final HashSet<String> imageFileList) {
        this.id = id;
        this.imageFileList = new HashSet<>(imageFileList);
    }

    /**
     * Return a copy for the hashset imageFileList.
     * @return copy of the hashset
     */
    public HashSet<String> getImageFileList() {
        return new HashSet<>(imageFileList);
    }

    /**
     * Set hashset imageFileList.
     * @param imageFileList The new imageFileList
     */
    public void setImageFileList(final HashSet<String> imageFileList) {
        this.imageFileList = new HashSet<>(imageFileList);
    }
}
