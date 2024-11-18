package com.example.demo.Controller;

import com.example.demo.Dtos.FileSearchResultDTO;
import com.example.demo.Dtos.FileUploadDTO;
import com.example.demo.Dtos.FileUploadResponseDTO;
import com.example.demo.Entity.FileEntity;
import com.example.demo.ServiceFile.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/files/local")
public class FileController {


    // Inject the FileStorageService to handle file uploads
    @Autowired
    private FileStorageService fileStorageService;



    // Logger to capture and print out information and error logs
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    /**
     * Endpoint to upload a file to Backblaze B2 storage.
     *
     * @param file - the file to be uploaded
     * @return ResponseEntity with the URL of the uploaded file or an error message
     */

    @Operation(summary = "Upload file", description = "Upload a file in our system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File Uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "File size exceeds or internal error")
    })
    @PostMapping("/upload")

    public ResponseEntity<String> uploadFile( @RequestParam("file") MultipartFile file,
                                              @RequestParam("employeeId") Long employeeId,
                                              @RequestParam("fileName") String FileName,
                                              @RequestParam("summary") String summary,
                                              @RequestParam("subFolderPath") String subFolderPath) {
        FileUploadDTO fileUploadDTO=new FileUploadDTO();
        fileUploadDTO.setFileName(FileName);
        fileUploadDTO.setSummary(summary);
        fileUploadDTO.setEmployeeId(employeeId);
        fileUploadDTO.setSubFolderPath(subFolderPath);
        logger.info("Received request to upload file: {}", file.getOriginalFilename());

        try {
            // Call the FileStorageService to upload the file and get the file URL
            FileUploadResponseDTO fileUploadResponseDTO= fileStorageService.uploadFile(file,fileUploadDTO);
            logger.info("File uploaded successfully. Accessible at URL: {}", fileUploadResponseDTO.getUrl());

            // Return the URL of the uploaded file as the response
            if(fileUploadResponseDTO.getResponse().equals("FAILED")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("FILE ALREADY EXIST");
            }else{
                return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully. File URL: " +fileUploadResponseDTO.getUrl() );
            }

        }catch (DataIntegrityViolationException e) {
            // This exception is thrown when a unique constraint is violated
            logger.error("Attempted to upload a duplicate file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("File already exists in the system.");
        }
        catch (Exception e) {
            // Log the error if the upload fails
            logger.error("File upload failed for file: {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
    @Operation(summary = "Upload Multiple file", description = "Upload Multiple file in our system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multiple File Uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Internal error")
    })

    @PostMapping("/uploadMultiple")
    public ResponseEntity<List<FileUploadResponseDTO>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("fileName") String fileName,
            @RequestParam("summary") String summary,
            @RequestParam(value = "subFolderPath", required = false) String subFolderPath) {

        logger.info("Received request to upload multiple files for Employee ID: {}", employeeId);


        // Create the DTO object for passing additional data
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setEmployeeId(employeeId);
        fileUploadDTO.setFileName(fileName);
        fileUploadDTO.setSummary(summary);
        fileUploadDTO.setSubFolderPath(subFolderPath);

        try {
            // Call the service to handle file uploads and get the response
            List<FileUploadResponseDTO> responseList = fileStorageService.uploadFiles(files, fileUploadDTO);

            logger.info("Successfully uploaded {} files for Employee ID: {}", files.size(), employeeId);
            return ResponseEntity.ok(responseList);

        }
       catch (Exception e) {
            logger.error("Error occurred while uploading files: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Search files", description = "Search files using SearchTerm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FileSearchResultDTO>> searchFiles(@RequestParam("query") String searchTerm) {

        logger.info("Received search request with term: {}", searchTerm);

        List<FileSearchResultDTO> matchingFiles = fileStorageService.searchFiles(searchTerm);

        if (matchingFiles.isEmpty()) {
            logger.info("No files found matching the search term: {}", searchTerm);
            return ResponseEntity.noContent().build();
        }

        logger.info("Returning {} files matching the search term: {}", matchingFiles.size(), searchTerm);
        return ResponseEntity.ok(matchingFiles);
    }
    @Operation(summary = "Search files", description = "Search files using SearchTerm and other filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @GetMapping("/search/filter")
    public ResponseEntity<List<FileSearchResultDTO>> searchFilesByFilters(
            @RequestParam("query") String searchTerm,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId,
            @RequestParam(value = "tagNames", required = false) List<String> tagNames) {

        List<FileSearchResultDTO> results = fileStorageService.searchFiles(searchTerm, startDate, endDate, uploaderId, tagNames);
        return ResponseEntity.ok(results);
    }



    @Operation(summary = "Search files", description = "Search all files by a particular tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<FileSearchResultDTO>> getFilesByTag(@PathVariable Long tagId) {

        logger.info("Received search request with Id: {}", tagId);

        List<FileSearchResultDTO> matchingFiles = fileStorageService.getFilesByTagId(tagId);

        if (matchingFiles.isEmpty()) {
            logger.info("No files found matching the tagId: {}", tagId);
            return ResponseEntity.noContent().build();
        }

        logger.info("Returning {} files matching the tagId: {}", matchingFiles.size(), tagId);
        return ResponseEntity.ok(matchingFiles);
    }

    @Operation(summary = "Search files", description = "Search all files in a particular date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<FileSearchResultDTO>> getFilesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<FileSearchResultDTO> filesWithinDateRange = fileStorageService.getFilesByDateRange(startDate, endDate);
        return ResponseEntity.ok(filesWithinDateRange);
    }

    @Operation(summary = "Get all files", description = "Fetch all files uploaded to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No files found")
    })
    @GetMapping("/all")
    public ResponseEntity<List<FileSearchResultDTO>> getAllFiles() {
        // Your logic here
        List<FileSearchResultDTO> allFiles = fileStorageService.getAllFiles();
        return ResponseEntity.ok(allFiles);
    }



}


    /**
     * Endpoint to retrieve file upload status and URL for a specific file.
     *
     * @paramfileName - the name of the file
     * @return ResponseEntity with the file URL or an error message
     */
//    @GetMapping("/download/{filename}")
//    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String uniqueFileName) {
//        try {
//            logger.info("Received download request for file: {}", uniqueFileName);
//
//            // Download the file as byte array
//            byte[] fileData = fileStorageService.downloadFile(uniqueFileName);
//
//            // Set headers for file download
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.setContentDispositionFormData("attachment", uniqueFileName);
//
//            logger.info("File download successful: {}", uniqueFileName);
//            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
//        } catch (IOException e) {
//            logger.error("File download failed", e);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }




