package com.project.ipms.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("image-items")
public class ImageFileEntry {

    /**
     * ID of an image for indexing.
     */
    @Id
    private String id;

    /**
     * File extension of an image (image type).
     */
    private String extension;

    /**
     * Original image filename when first uploading.
     */
    private String originalFileName;
}
