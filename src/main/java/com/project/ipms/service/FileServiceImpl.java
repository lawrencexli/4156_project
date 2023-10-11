/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.project.ipms.exception.FileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


@Service
public class FileServiceImpl implements FileService {

    /**
     * Get the bucket name from application.properties.
     */
    @Value("${gcp.bucket.name}")
    private String bucketName;

    /**
     * Storage object for calling methods.
     */
    private final Storage storage;

    /**
     * Constructor for FileService.
     * @param storageObject Google cloud storage object
     */
    @Autowired
    public FileServiceImpl(final Storage storageObject) {
        this.storage = storageObject;
    }

    /**
     * Download file from Google Cloud Storage.
     * @param fileName Name of file
     * @return Byte Array File
     */
    @Override
    public ByteArrayResource downloadFile(final String fileName) {
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new FileNotFoundException("File does not exist");
        }
        return new ByteArrayResource(blob.getContent());
    }

    /**
     * Upload file to Google Cloud Storage.
     * @param file Representation of a file received in a multipart request
     * @throws IOException If file does not exist or filename is inappropriate
     */
    @Override
    public void uploadFile(final MultipartFile file) throws IOException {
        BlobId blobId = BlobId.of(bucketName, Objects.requireNonNull(file.getOriginalFilename()));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
    }
}
