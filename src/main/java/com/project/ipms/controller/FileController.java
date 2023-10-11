/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.exception.FileNotFoundException;
import com.project.ipms.exception.InvalidCredentialsException;
import com.project.ipms.mongodb.ClientEntry;
import com.project.ipms.mongodb.ClientRepository;
import com.project.ipms.service.FileService;
import com.project.ipms.util.ImageFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;

@RestController
@RequestMapping("/api")
public class FileController {
    /**
     * MongoDB repo.
     */
    private final ClientRepository ipmsMongoRepo;

    /**
     * A file service object for calling methods.
     */
    private final FileService fileService;

    /**
     * Constructor for FileController.
     * Autowire and link MongoDB repo.
     * @param fileService A file service object for calling methods
     * @param ipmsMongoRepo The object for calling MongoDB database service
     */
    @Autowired
    public FileController(final FileService fileService,
                          final ClientRepository ipmsMongoRepo) {
        this.fileService = fileService;
        this.ipmsMongoRepo = ipmsMongoRepo;
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
            throw new BadRequestException("File has no content or file is null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or is null");
        }
        // Retrieve client entry by ID.
        ClientEntry clientEntry = ipmsMongoRepo.findClientEntryById(id);
        if (clientEntry == null) {
            throw new InvalidCredentialsException("Invalid Client ID");
        }
        String originalFileName = file.getOriginalFilename();
        ImageFileUtil.checkFileValid(originalFileName);
        // Retrieve image file list by client entry.
        HashSet<String> imageFileList = clientEntry.getImageFileList();
        if (imageFileList.contains(originalFileName)) {
            throw new BadRequestException("Filename already exists");
        }
        // Upload the file and update the client entry to MongoDB.
        fileService.uploadFile(file, id);
        imageFileList.add(originalFileName);
        clientEntry.setImageFileList(imageFileList);
        ipmsMongoRepo.save(clientEntry);
        // Return success response
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
            throw new BadRequestException("Filename is null or empty");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or is null");
        }
        // Retrieve client entry by ID.
        ClientEntry clientEntry = ipmsMongoRepo.findClientEntryById(id);
        if (clientEntry == null) {
            throw new InvalidCredentialsException("Invalid Client ID");
        }
        HashSet<String> imageFileList = clientEntry.getImageFileList();
        if (!imageFileList.contains(fileName)) {
            throw new FileNotFoundException("File does not exist");
        }
        // Download the file.
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
        ClientEntry newClientEntry = new ClientEntry(null, new HashSet<>());
        ipmsMongoRepo.insert(newClientEntry);
        ApiResponse response = new ApiResponse();
        response.setResponseMessage(newClientEntry.getId());
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }
}
