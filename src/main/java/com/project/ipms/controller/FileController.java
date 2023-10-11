/**
 * Code for Spring Boot + Google Cloud Storage integration
 * From: https://www.knowledgefactory.net/2023/03/google-cloud-storage-spring-boot-file-upload-download-and-delete.html
 */

package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.service.FileService;
import com.project.ipms.util.ImageFileUtil;
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
     * Upload a file.
     * @param file Representation of a file received in a multipart request
     * @return A success message
     */
    @PostMapping("upload")
    public ApiResponse uploadFile(@RequestParam final MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File has no content or file is null");
        }
        ImageFileUtil.checkFileValid(file.getOriginalFilename());
        fileService.uploadFile(file);
        ApiResponse response = new ApiResponse();
        response.setResponseMessage("File uploaded successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    /**
     * Download file from Google Cloud Storage.
     * @param fileName Name of file
     * @return Byte array file content
     */
    @GetMapping("download")
    public ResponseEntity<Resource> downloadFile(@RequestParam final String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("Filename is null or empty");
        }
        ByteArrayResource resource = fileService.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");

        return ResponseEntity.ok().
                contentType(MediaType.APPLICATION_OCTET_STREAM).
                headers(headers).body(resource);
    }
}
