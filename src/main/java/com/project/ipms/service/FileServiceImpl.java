/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.project.ipms.exception.CriticalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


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
     * @param storage Google cloud storage object
     */
    @Autowired
    public FileServiceImpl(final Storage storage) {
        this.storage = storage;
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
            throw new CriticalServerException(
                    "CRITICAL ERROR: File does not exist on GCP Bucket but exists in MongoDB records"
            );
        }
        return new ByteArrayResource(blob.getContent());
    }

    /**
     * Upload file to Google Cloud Storage.
     * @param file Representation of a file received in a multipart request
     * @throws IOException If file does not exist, file has no content, or filename is inappropriate
     */
    @Override
    public void uploadFile(final MultipartFile file,
                           final String repoName) throws IOException {
        BlobId blobId = BlobId.of(bucketName,
                repoName + "/" + file.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
    }
}
