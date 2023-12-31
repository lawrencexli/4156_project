/**
 * Code for Spring Boot + Google Cloud Storage integration.
 */

package com.project.ipms.controller;

import com.project.ipms.exception.BadRequestException;
import com.project.ipms.mongodb.MongoDBService;
import com.project.ipms.service.FileService;
import com.project.ipms.service.ImageService;
import com.project.ipms.util.ImageFileUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @SuppressFBWarnings("EI_EXPOSE_REP2")
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
    @CrossOrigin
    @PostMapping("upload")
    public ApiResponse uploadFile(@RequestParam final MultipartFile file,
                                  @RequestHeader final String id) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File has no content or is null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        if (ImageIO.read(file.getInputStream()) == null) {
            String imageValidationErrorMsg = "Image file validation failed: "
                    + "The file could be corrupted or is not an image file";
            throw new BadRequestException(imageValidationErrorMsg);
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
    @CrossOrigin
    @GetMapping("download")
    public ResponseEntity<Resource> downloadFile(@RequestParam final String fileName,
                                                 @RequestHeader final String id) {
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
    @CrossOrigin
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
    @CrossOrigin
    @PutMapping("transparent")
    public ApiResponse imageTransparent(@RequestPart final String target,
                                        @RequestPart final String result,
                                        @RequestHeader final String id,
                                        @RequestPart final String alpha) throws IOException {
        // Check if all inputs are valid
        if (target == null || target.isBlank()
                || result == null || result.isBlank()) {
            throw new BadRequestException("Target filename or result filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        float alphaVal = Float.parseFloat(alpha);
        if (alphaVal < 0 || alphaVal > 1) {
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
                alphaVal,
                targetFileExtension
        );
        // Upload the result image
        return uploadResult(result, id, resultImage, resultFileExtension);
    }

    /**
     * Crop the image.
     * @param target     Target filename in client's repository
     * @param result     Result filename after processing
     * @param id         Client ID credential
     * @param x          The X coordinate of the upper-left corner of the specified
     *                   rectangular region
     * @param y          The X coordinate of the upper-left corner of the specified
     *                   rectangular region
     * @param width      The width of the specified rectangular region
     * @param height     The height of the specified rectangular region
     * @return           Processed image in BufferedImage format
     */
    @CrossOrigin
    @PutMapping("crop")
    public ApiResponse imageCrop(@RequestPart final String target,
                                 @RequestPart final String result,
                                 @RequestHeader final String id,
                                 @RequestPart final String x,
                                 @RequestPart final String y,
                                 @RequestPart final String width,
                                 @RequestPart final String height) throws IOException {
        // Check if all inputs are valid
        if (target == null || target.isBlank()
                || result == null || result.isBlank()) {
            throw new BadRequestException("Target filename or result filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
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
        // Convert String to int
        int xVal = Integer.parseInt(x);
        int yVal = Integer.parseInt(y);
        int widthVal = Integer.parseInt(width);
        int heightVal = Integer.parseInt(height);
        // Retrieve and process image file from storage
        ByteArrayResource resource = fileService.downloadFile(id + "/" + target);
        BufferedImage targetImage = ImageIO.read(resource.getInputStream());
        // Check if the x, y, width, height are correct
        if (xVal < 0 || xVal > targetImage.getWidth()) {
            throw new BadRequestException("The x value should be in the range of 0 to the width of the target image");
        }
        if (yVal < 0 || yVal > targetImage.getHeight()) {
            throw new BadRequestException("The y value should be in the range of 0 to the height of the target image");
        }
        if (widthVal <= 0 || widthVal > targetImage.getWidth() - xVal) {
            throw new BadRequestException("The width value should be from 1 to (target image's width - x)");
        }
        if (heightVal <= 0 || heightVal > targetImage.getHeight() - yVal) {
            throw new BadRequestException("The height value should be from 1 to (target image's height - y)");
        }
        BufferedImage resultImage = imageService.imageCropping(
                targetImage,
                xVal, yVal, widthVal, heightVal,
                targetFileExtension
        );
        // Upload the result image
        return uploadResult(result, id, resultImage, resultFileExtension);
    }

    /**
     * Overlay InputImage1 over InputImage2.
     * @param target1 Base image filename in client's repository
     * @param target2 Image to be overlayed over the base image filename in client's repository
     * @param result  Result filename after processing
     * @param id      Client ID credential
     * @param x       The X coordinate of the upper-left corner for the overlay
     * @param y       The Y coordinate of the upper-left corner for the overlay
     * @return Processed image in BufferedImage format
     */
    @CrossOrigin
    @PutMapping("overlay")
    public ApiResponse imageOverlay(@RequestPart final String target1,
                                    @RequestPart final String target2,
                                    @RequestPart final String result,
                                    @RequestHeader final String id,
                                    @RequestPart final String x,
                                    @RequestPart final String y) throws IOException {
        // Check if all inputs are valid
        if (target1 == null || target1.isBlank() || target2 == null || target2.isBlank()
                || result == null || result.isBlank()) {
            throw new BadRequestException("Target filenames or result filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        // Check if the target files exist in client's repository,
        // and if the result filename is available (avoid overwriting)
        // and if the file extensions for targets and result are consistent
        mongoDBService.mongoDBOperationCheck(id, target1, result);
        mongoDBService.mongoDBOperationCheck(id, target2, result);
        String target1FileExtension = ImageFileUtil.checkFileValid(target1);
        String target2FileExtension = ImageFileUtil.checkFileValid(target2);
        String resultFileExtension = ImageFileUtil.checkFileValid(result);
        if (!target1FileExtension.equals(resultFileExtension) || !target2FileExtension.equals(resultFileExtension)) {
            throw new BadRequestException("Target file extension is different from result file extension");
        }
        // Convert Strings
        int xVal = Integer.parseInt(x);
        int yVal = Integer.parseInt(y);
        // Retrieve and process image file from storage
        ByteArrayResource resource1 = fileService.downloadFile(id + "/" + target1);
        ByteArrayResource resource2 = fileService.downloadFile(id + "/" + target2);
        BufferedImage targetImage1 = ImageIO.read(resource1.getInputStream());
        BufferedImage targetImage2 = ImageIO.read(resource2.getInputStream());
        // Check if the x, y, alpha are correct
        if (xVal < 0 || xVal > targetImage1.getWidth()) {
            throw new BadRequestException("The x value should be in the range of 0 to the width of the target image");
        }
        if (yVal < 0 || yVal > targetImage1.getHeight()) {
            throw new BadRequestException("The y value should be in the range of 0 to the height of the target image");
        }
        BufferedImage resultImage = imageService.imageOverlay(
                targetImage1,
                targetImage2,
                xVal, yVal,
                target1FileExtension
        );
        // Upload the result image
        return uploadResult(result, id, resultImage, resultFileExtension);
    }

    /**
    * Change image saturation.
    * @param target Target filename in client's repository
    * @param result Result filename after processing
    * @param id Client ID credential
    * @param saturationCoeff Alpha value for transparency
    * @return json response for operation success
    * @throws IOException For exceptions in reading and writing image bytes
    */
    @CrossOrigin
    @PutMapping("saturation")
    public ApiResponse imageSaturate(@RequestPart final String target,
                                     @RequestPart final String result,
                                     @RequestHeader final String id,
                                     @RequestPart final String saturationCoeff) throws IOException {
        // Check if all inputs are valid
        if (target == null || target.isBlank()
                || result == null || result.isBlank()) {
            throw new BadRequestException("Target filename or result filename is empty or null");
        }
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Client ID is missing or null");
        }
        float saturationVal = Float.parseFloat(saturationCoeff);
        if (saturationVal < 0 || saturationVal > 255) {
            throw new BadRequestException("The saturation coefficient should be in the range of 0 to 255");
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
        BufferedImage resultImage = imageService.imageSaturation(
                targetImage,
                saturationVal,
                targetFileExtension
        );
        // Upload the result image
        return uploadResult(result, id, resultImage, resultFileExtension);
    }

    /**
     * Wrapper function for uploading processed image file.
     * NOTE: This wrapper method will be tested from other methods from this class
     * like imageCrop() and imageTransparent() as part of the image feature controller test.
     * @param result The name of the result image file
     * @param id Client ID credential
     * @param resultImage BufferedImage object of processed image
     * @param resultFileExtension The file extension of processed image file
     * @return ApiResponse object for client end
     * @throws IOException For errors in reading/writing image bytes to file
     */
    private ApiResponse uploadResult(final String result,
                                     final String id,
                                     final BufferedImage resultImage,
                                     final String resultFileExtension) throws IOException {
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
