package com.example.erpsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Upload directory created: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            logger.error("Could not create upload directory: {}", ex.getMessage());
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File stored successfully: {}", fileName);
            return fileName;
        } catch (IOException ex) {
            logger.error("Could not store file: {}", ex.getMessage());
            throw new RuntimeException("Could not store file", ex);
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                return false;
            }
            
            // Extract filename from path
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path fileToDelete = this.fileStorageLocation.resolve(fileName);
            
            boolean deleted = Files.deleteIfExists(fileToDelete);
            if (deleted) {
                logger.info("File deleted successfully: {}", fileName);
            } else {
                logger.warn("File not found for deletion: {}", fileName);
            }
            return deleted;
        } catch (IOException ex) {
            logger.error("Could not delete file: {}", ex.getMessage());
            return false;
        }
    }

    public Path loadFile(String filename) {
        return fileStorageLocation.resolve(filename);
    }
}