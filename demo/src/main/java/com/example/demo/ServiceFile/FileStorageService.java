package com.example.demo.ServiceFile;

import com.example.demo.Dtos.FileSearchResultDTO;
import com.example.demo.Dtos.FileUploadDTO;
import com.example.demo.Dtos.FileUploadResponseDTO;
import com.example.demo.Entity.Employee;
import com.example.demo.Entity.FileComments;
import com.example.demo.Entity.FileEntity;
import com.example.demo.Entity.Tag;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.FileEntityRepository;
import com.example.demo.Service.EmployeeService;
import com.example.demo.Utility.FileUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;

@Service
public class FileStorageService {

//    // Injecting the S3Client bean created in BackblazeB2Config
//    @Autowired
//    private S3Client s3Client;

    // Injecting the bucket name from application.properties
//    @Value("${backblaze.b2.bucket-name}")
//    private String bucketName;

        @Value("${file.upload-dir}")
        private String uploadDir;

        @Autowired
        private EmployeeRepository employeeRepository;

        @Autowired
        private FileEntityRepository fileEntityRepository;

        @Autowired
        private FileUtility fileUtility;

        private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

       private final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);
    /**
     * Method to upload a file to Backblaze B2 and return the file link
     *
     * @param file          - Multipart file uploaded by the user
     * @param fileUploadDTO
     * @return The file’s storage link on Backblaze B2
     * @throws IOException If an error occurs during file processing
     */
    @Transactional
    public FileUploadResponseDTO uploadFile(MultipartFile file, FileUploadDTO fileUploadDTO) throws IOException, NoSuchAlgorithmException {
        LOG.info("Request received for new Upload File");

        Employee employee = employeeRepository.findById(fileUploadDTO.getEmployeeId())
                .orElseThrow(() -> new IOException("Employee not found with ID: " + fileUploadDTO.getEmployeeId()));

        if (file.getSize() > MAX_FILE_SIZE) {
            LOG.warn("File size exceeds the 50MB limit: {}", file.getOriginalFilename());
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }

        // Ensure the upload directory exists
        // Define the full path with nested subfolders
        String folderPath = uploadDir +"/"+fileUploadDTO.getSubFolderPath() + "/";
        Path uploadPath = Paths.get(folderPath);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileHash = fileUtility.calculateFileHash(file);

        //Redundant
//        if (fileEntityRepository.existsByFileNameAndFileHash(originalFileName, fileHash)) {
//            LOG.info("File with name '{}' and the same content already exists", originalFileName);
//            FileUploadResponseDTO fileUploadResponseDTO=new FileUploadResponseDTO();
//            fileUploadResponseDTO.setResponse("FAILED");
//            fileUploadResponseDTO.setUrl(null);
//            return fileUploadResponseDTO;
//        }
        // Generate a unique filename by appending a UUID to the original file name
        String uniqueFileName = file.getOriginalFilename();
        File destinationFile = new File(folderPath + uniqueFileName);
        //Path filePath = uploadPath.resolve(uniqueFileName);

        // Save the file locally
        //file.transferTo(destinationFile);
        Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        LOG.info("File saved locally with name: {} at path: {}", uniqueFileName, destinationFile.toString());
        LOG.info("File uploaded to local storage at path {}: {}", fileUploadDTO.getSubFolderPath(), destinationFile.getAbsolutePath());

        // Create and populate FileEntity
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUrl(destinationFile.getAbsolutePath());
        fileEntity.setFileName(fileUploadDTO.getFileName());
        fileEntity.setSummary(fileUploadDTO.getSummary());
        fileEntity.setFileHash(fileHash); // Store the hash for future comparisons

        // Bidirectional relationship
        employee.getFiles().add(fileEntity);
        fileEntity.setEmployee(employee);

        // Save FileEntity in repository
        fileEntityRepository.save(fileEntity);
        LOG.info("File successfully saved in repository");

        // Prepare response
        FileUploadResponseDTO fileUploadResponseDTO = new FileUploadResponseDTO();
        fileUploadResponseDTO.setUrl(destinationFile.getAbsolutePath());
        fileUploadResponseDTO.setResponse("File Uploaded Successfully");

        return fileUploadResponseDTO;
    }

    @Transactional
    public List<FileUploadResponseDTO> uploadFiles(List<MultipartFile> files, FileUploadDTO fileUploadDTO) throws IOException, NoSuchAlgorithmException {
        LOG.info("Request received to upload multiple files");

        // Retrieve the employee associated with the files
        Employee employee = employeeRepository.findById(fileUploadDTO.getEmployeeId())
                .orElseThrow(() -> new IOException("Employee not found with ID: " + fileUploadDTO.getEmployeeId()));

        // Define directory path for file storage
        String folderPath = uploadDir + "/" + fileUploadDTO.getSubFolderPath() + "/";
        Path uploadPath = Paths.get(folderPath);

        // Create directory structure if it doesn't exist
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
            LOG.info("Created directory path for file uploads: {}", folderPath);
        }

        List<FileUploadResponseDTO> responseList = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();

            // File size check
            if (file.getSize() > MAX_FILE_SIZE) {
                LOG.warn("File size exceeds the 50MB limit: {}", originalFileName);
                throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
            }

            // Calculate file hash for duplicate detection
            String fileHash = fileUtility.calculateFileHash(file);

            // Generate a unique filename by retaining the original file name
            String uniqueFileName = originalFileName;
            File destinationFile = new File(folderPath + uniqueFileName);

            // Save file to the local directory
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOG.info("File saved locally with name: {} at path: {}", uniqueFileName, destinationFile.toString());

            // Check if the file already exists in the database based on fileHash (handled by the unique constraint)
            FileEntity fileEntity = new FileEntity();
            fileEntity.setUrl(destinationFile.getAbsolutePath());
            fileEntity.setFileName(uniqueFileName);
            fileEntity.setSummary(fileUploadDTO.getSummary());
            fileEntity.setFileHash(fileHash); // Unique constraint prevents duplicates

            // Establish relationship between Employee and File
            employee.getFiles().add(fileEntity);
            fileEntity.setEmployee(employee);

            // Save file metadata in the repository
            fileEntityRepository.save(fileEntity);
            LOG.info("File metadata for '{}' successfully saved to repository", uniqueFileName);

            // Add success response for each file
            FileUploadResponseDTO fileUploadResponseDTO = new FileUploadResponseDTO();
            fileUploadResponseDTO.setUrl(destinationFile.getAbsolutePath());
            fileUploadResponseDTO.setResponse("File Uploaded Successfully");
            responseList.add(fileUploadResponseDTO);
        }

        LOG.info("All files uploaded successfully");
        return responseList;
    }


    public List<FileSearchResultDTO> searchFiles(String searchTerm) {

        LOG.info("Initiating search for files with term: {}", searchTerm);

        List<FileEntity> matchingFiles = fileEntityRepository.searchFilesByKeyword(searchTerm);

        List<FileSearchResultDTO> fileSearchResultDTOS=new ArrayList<>();

        for(FileEntity f:matchingFiles){

            List<String> tags = new ArrayList<>();
            for (Tag tag : f.getTags()) {
                tags.add(tag.getName());
            }

            List<String> comments = new ArrayList<>();
            for (FileComments comment : f.getComments()) {
                comments.add(comment.getCommentText());
            }

            FileSearchResultDTO fileSearchResultDTO=new FileSearchResultDTO();
            fileSearchResultDTO.setFileUrl(f.getUrl());
            fileSearchResultDTO.setTags(tags);
            fileSearchResultDTO.setComments(comments);
            fileSearchResultDTO.setFileName(f.getFileName());
            fileSearchResultDTO.setUploadedBy(f.getEmployee().getName());
            fileSearchResultDTO.setUploadedAt(f.getCreatedAt());

            fileSearchResultDTOS.add(fileSearchResultDTO);

        }

        LOG.info("Found {} files matching search term '{}'", matchingFiles.size(), searchTerm);
        return fileSearchResultDTOS;
    }


    public List<FileSearchResultDTO> getFilesByTagId(Long tagId) {

        LOG.info("Initiating search for files with tagId: {}", tagId);
        List<FileEntity> matchingFiles = fileEntityRepository.findByTagsId(tagId);


        List<FileSearchResultDTO> fileSearchResultDTOS=new ArrayList<>();

        for(FileEntity f:matchingFiles){

            List<String> tags = new ArrayList<>();
            for (Tag tag : f.getTags()) {
                tags.add(tag.getName());
            }

            List<String> comments = new ArrayList<>();
            for (FileComments comment : f.getComments()) {
                comments.add(comment.getCommentText());
            }

            FileSearchResultDTO fileSearchResultDTO=new FileSearchResultDTO();
            fileSearchResultDTO.setFileUrl(f.getUrl());
            fileSearchResultDTO.setTags(tags);
            fileSearchResultDTO.setComments(comments);
            fileSearchResultDTO.setFileName(f.getFileName());
            fileSearchResultDTO.setUploadedBy(f.getEmployee().getName());
            fileSearchResultDTO.setUploadedAt(f.getCreatedAt());

            fileSearchResultDTOS.add(fileSearchResultDTO);

        }
        LOG.info("Found {} files matching search term '{}'", matchingFiles.size(),tagId);
        return fileSearchResultDTOS;
    }

    public List<FileSearchResultDTO> getFilesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<FileEntity> files = fileEntityRepository.findByCreatedAtBetween(startDate,endDate);

        List<FileSearchResultDTO> fileSearchResultDTOS=new ArrayList<>();

        for (FileEntity f : files) {

            List<String> tags = new ArrayList<>();
            for (Tag tag : f.getTags()) {
                tags.add(tag.getName());
            }

            List<String> comments = new ArrayList<>();
            for (FileComments comment : f.getComments()) {
                comments.add(comment.getCommentText());
            }

            FileSearchResultDTO fileSearchResultDTO=new FileSearchResultDTO();
            fileSearchResultDTO.setFileUrl(f.getUrl());
            fileSearchResultDTO.setTags(tags);
            fileSearchResultDTO.setComments(comments);
            fileSearchResultDTO.setFileName(f.getFileName());
            fileSearchResultDTO.setUploadedBy(f.getEmployee().getName());
            fileSearchResultDTO.setUploadedAt(f.getCreatedAt());

            fileSearchResultDTOS.add(fileSearchResultDTO);

        }
        return fileSearchResultDTOS;

    }

    public List<FileSearchResultDTO> searchFiles(String searchTerm, LocalDate startDate, LocalDate endDate, Long uploaderId, List<String> tagNames) {
        long tagCount = (tagNames != null) ? tagNames.size() : 0;
        List<FileEntity> files = fileEntityRepository.searchFilesWithFilters(searchTerm, startDate, endDate, uploaderId, tagNames);
        List<FileSearchResultDTO> results = new ArrayList<>();

        for (FileEntity file : files) {
            FileSearchResultDTO dto = new FileSearchResultDTO();
            dto.setFileName(file.getFileName());
            dto.setFileUrl(file.getUrl());
            dto.setUploadedBy(file.getEmployee().getName());
            dto.setUploadedAt(file.getCreatedAt());
            dto.setTags(new ArrayList<>());
            for (Tag tag : file.getTags()) {
                dto.getTags().add(tag.getName());
            }
            dto.setComments(new ArrayList<>());
            for (FileComments comment : file.getComments()) {
                dto.getComments().add(comment.getCommentText());
            }
            results.add(dto);
        }
        return results;
    }

    public List<FileSearchResultDTO> getAllFiles() {
        List<FileEntity> files = fileEntityRepository.findAll();
        List<FileSearchResultDTO> results = new ArrayList<>();

        for (FileEntity file : files) {
            FileSearchResultDTO dto = new FileSearchResultDTO();
            dto.setFileName(file.getFileName());
            dto.setFileUrl(file.getUrl());
            dto.setUploadedBy(file.getEmployee().getName());
            dto.setUploadedAt(file.getCreatedAt());
            dto.setTags(new ArrayList<>());
            for (Tag tag : file.getTags()) {
                dto.getTags().add(tag.getName());
            }
            dto.setComments(new ArrayList<>());
            for (FileComments comment : file.getComments()) {
                dto.getComments().add(comment.getCommentText());
            }
            results.add(dto);
        }
        return results;


    }


//    public byte[] downloadFile(String uniqueFileName) throws IOException {
//        Path filePath = Paths.get(uploadDir).resolve(uniqueFileName);
//
//        if (!Files.exists(filePath)) {
//            throw new IOException("File not found: " + uniqueFileName);
//        }
//
//        // Read and return the file as byte array
//        return Files.readAllBytes(filePath);
//    }


}