/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.controller;

import com.project.ipms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    /**
     * A file service object for calling methods.
     */
    private final FileService fileService;

    /**
     * Constructor for FileController.
     * @param fileServiceObject A file service object for calling methods
     */
    @Autowired
    public FileController(final FileService fileServiceObject) {
        this.fileService = fileServiceObject;
    }

    /**
     * List all filename in the bucket storage.
     * @return List of files as string
     */
    @GetMapping
    public ApiResponse listOfFiles() {
        List<String> files = fileService.listOfFiles();
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(files.toString());
        response.setStatusCode(HttpStatus.OK.value());
        response.setZonedDateTime(ZonedDateTime.now());
        return response;
    }

    /**
     * Upload a file.
     * @param file Representation of a file received in a multipart request
     * @return A success message
     */
    @PostMapping("upload")
    public ApiResponse uploadFile(@RequestParam final MultipartFile file) throws IOException {
        fileService.uploadFile(file);
        ApiResponse response = new ApiResponse();
        response.setResponseMessage("File uploaded successfully");
        response.setStatusCode(HttpStatus.OK.value());
        response.setZonedDateTime(ZonedDateTime.now());
        return response;
    }

    /**
     * Download file from Google Cloud Storage.
     * @param fileName Name of file
     * @return Byte array file content
     */
    @GetMapping("download")
    public ResponseEntity<Resource> downloadFile(@RequestParam final String fileName) {
        ByteArrayResource resource = fileService.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");

        return ResponseEntity.ok().
                contentType(MediaType.APPLICATION_OCTET_STREAM).
                headers(headers).body(resource);
    }
}
