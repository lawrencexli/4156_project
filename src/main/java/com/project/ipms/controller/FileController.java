/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileController {

    /**
     * A file service object for calling methods.
     */
    private final FileService fileService;

    /**
     * A MongoDB service object for calling methods.
     */
    private final MongoDBService mongoDBService;

    /**
     * Constructor for FileController.
     * Autowire and link MongoDB repo.
     * @param fileService A file service object for calling methods
     * @param mongoDBService A MongoDB database service for calling methods
     */
    @Autowired
    public FileController(final FileService fileService,
                          final MongoDBService mongoDBService) {
        this.fileService = fileService;
        this.mongoDBService = mongoDBService;
    }

    /**
     * Upload a file.
     * @param file Representation of a file received in a multipart request
     * @param id Client id
     * @return A success message
     */
    @PostMapping("upload")
    public ApiResponse uploadFile(@RequestParam final MultipartFile file,
                                  @RequestParam final String id) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File has no content or is null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        mongoDBService.uploadFile(id, file.getOriginalFilename());
        fileService.uploadFile(file, id);
        ApiResponse response = new ApiResponse();
        response.setResponseMessage("File uploaded successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    /**
     * Download file from Google Cloud Storage.
     * @param fileName Name of file
     * @param id client ID
     * @return Byte array file content
     */
    @GetMapping("download")
    public ResponseEntity<Resource> downloadFile(@RequestParam final String fileName,
                                                 @RequestParam final String id) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("Filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        mongoDBService.mongoDBFileCheck(id, fileName);
        ByteArrayResource resource = fileService.downloadFile(id + "/" + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");

        return ResponseEntity.ok().
                contentType(MediaType.APPLICATION_OCTET_STREAM).
                headers(headers).body(resource);
    }

    /**
     * Generate a new client ID.
     * @return json response containing generated client ID
     */
    @GetMapping("generate")
    public ApiResponse generateClientID() {
        String newClientID = mongoDBService.generateNewKey();
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(newClientID);
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }
}
