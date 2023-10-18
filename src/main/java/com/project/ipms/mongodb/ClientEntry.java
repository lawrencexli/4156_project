package com.project.ipms.mongodb;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Getter
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
    private final HashSet<String> imageFileList;

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
     * Add a new filename entry to hashset.
     * @param fileName the filename
     */
    public void addToImageFileList(final String fileName) {
        imageFileList.add(fileName);
    }

    /**
     * Check if a filename entry is in the hashset records.
     * @param fileName the filename
     * @return True if filename is in the list, False if otherwise
     */
    public boolean fileNameInImageFileList(final String fileName) {
        return imageFileList.contains(fileName);
    }

    /**
     * Get a list of file.
     * @return An imageFileList hashset
     */
    public HashSet<String> getImageFileList() {
        return new HashSet<>(imageFileList);
    }
}
