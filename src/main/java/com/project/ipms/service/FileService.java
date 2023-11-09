/**
 * Code for Spring Boot + Google Cloud Storage integration.
 */

package com.project.ipms.service;

import org.springframework.core.io.ByteArrayResource;

import java.util.List;

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

    /**
     * Delete a file or a directory by its name.
     * @param name Name of the file or directory
     * @return True if the file or directory was deleted successfully,
     * or False if the file or directory was not found.
     */
    boolean deleteFile(String name);

    /**
     * Return a list of files in a client's directory.
     * @param id Client ID for the directory.
     * @return A list of filenames.
     */
    List<String> listOfFiles(String id);
}
