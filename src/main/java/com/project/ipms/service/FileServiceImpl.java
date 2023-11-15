/**
 * Code for Spring Boot + Google Cloud Storage integration.
 */

package com.project.ipms.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.project.ipms.exception.CriticalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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
     * @param fileName fileName
     * @param fileContentType content type for file
     * @param fileBytes the file content bytes
     * @param repoName The client ID used to create folder name in GCP bucket
     */
    @Override
    public void uploadFile(final String fileName,
                           final String fileContentType,
                           final byte[] fileBytes,
                           final String repoName) {
        BlobId blobId = BlobId.of(bucketName,
                repoName + "/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(fileContentType).build();
        storage.create(blobInfo, fileBytes);
    }

    /**
     * Delete a file or a directory by its name.
     * @param name Name of the file or directory
     * @return True if the file or directory was deleted successfully,
     * or False if the file or directory was not found.
     */
    @Override
    public boolean deleteFile(final String name) {
        Blob blob = storage.get(bucketName, name);
        if (blob == null) {
            return false;
        }
        return blob.delete();
    }

    /**
     * Return a list of files in a client's directory.
     * @param id Client ID for the directory.
     * @return A list of filenames.
     */
    @Override
    public List<String> listOfFiles(final String id) {
        List<String> list = new ArrayList<>();
        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(id + "/"),
                Storage.BlobListOption.currentDirectory());
        for (Blob blob : blobs.iterateAll()) {
            list.add(blob.getName());
        }
        return list;
    }
}
