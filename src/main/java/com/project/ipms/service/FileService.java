/**
 * Code for Spring Boot + Google Cloud Storage integration.
 */

package com.project.ipms.service;

import org.springframework.core.io.ByteArrayResource;

public interface FileService {
    /**
     * Download file from Google Cloud Storage.
     * @param fileName Name of file
     * @return Byte Array File
     */
    ByteArrayResource downloadFile(String fileName);

    /**
     * Upload file to Google Cloud Storage.
     * @param fileName fileName
     * @param fileContentType content type for file
     * @param fileBytes the file content bytes
     * @param repoName The client ID used to create folder name in GCP bucket
     */
    void uploadFile(String fileName,
                    String fileContentType,
                    byte[] fileBytes,
                    String repoName);
}
