/**
 * Code for Spring Boot + Google Cloud Storage integration.
 */

package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;

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
     * An image processing service object for calling methods.
     */
    private final ImageService imageService;

    /**
     * Constructor for FileController.
     * Autowire and link MongoDB repo.
     * @param fileService A file service object for calling methods
     * @param mongoDBService A MongoDB database service for calling methods
     * @param imageService An image service for calling methods
     */
    @Autowired
    public FileController(final FileService fileService,
                          final MongoDBService mongoDBService,
                          final ImageService imageService) {
        this.fileService = fileService;
        this.mongoDBService = mongoDBService;
        this.imageService = imageService;
    }

    /**
     * Upload a file.
     * @param file Representation of a file received in a multipart request
     * @param id Client id
     * @return A success message
     * @throws IOException For exceptions in reading and writing image bytes
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
        fileService.uploadFile(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                id
        );
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

    /**
     * Make image transparent.
     * @param target Target filename in client's repository
     * @param result Result filename after processing
     * @param id Client ID credential
     * @param alpha Alpha value for transparency
     * @return json response for operation success
     * @throws IOException For exceptions in reading and writing image bytes
     */
    @PutMapping("transparent")
    public ApiResponse imageTransparent(@RequestParam final String target,
                                        @RequestParam final String result,
                                        @RequestParam final String id,
                                        @RequestParam final float alpha) throws IOException {
        // Check if all inputs are valid
        if (target == null || target.isBlank()
                || result == null || result.isBlank()) {
            throw new BadRequestException("Target filename or result filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        if (alpha < 0 || alpha > 1) {
            throw new BadRequestException("The alpha value should be in the range of 0 to 1");
        }
        // Check if the target file exists in client's repository,
        // and if the result filename is available (avoid overwriting)
        // and if the file extensions for target and result are consistent
        mongoDBService.mongoDBOperationCheck(id, target, result);
        String targetFileExtension = ImageFileUtil.checkFileValid(target);
        String resultFileExtension = ImageFileUtil.checkFileValid(result);
        if (!targetFileExtension.equals(resultFileExtension)) {
            throw new BadRequestException("Target file extension is different from result file extension");
        }
        // Retrieve and process image file from storage
        ByteArrayResource resource = fileService.downloadFile(id + "/" + target);
        BufferedImage targetImage = ImageIO.read(resource.getInputStream());
        BufferedImage resultImage = imageService.imageTransparency(
                targetImage,
                alpha,
                targetFileExtension
        );
        // Upload the result image
        mongoDBService.uploadFile(id, result);
        ByteArrayOutputStream byteImageOutput = new ByteArrayOutputStream();
        // resultFileExtension looks like ".jpg", ".png". Therefore, need to remove the dot "."
        ImageIO.write(
                resultImage,
                resultFileExtension.substring(1),
                byteImageOutput
        );

        fileService.uploadFile(
                result,
                URLConnection.getFileNameMap().getContentTypeFor(result),
                byteImageOutput.toByteArray(),
                id
        );

        ApiResponse response = new ApiResponse();
        response.setResponseMessage("Operation success");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }
}
