package com.controle.fechamentocaixa.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageService {
    // Google Drive operations
    String uploadToGoogleDrive(MultipartFile file) throws IOException;
    void deleteFromGoogleDrive(String fileId) throws IOException;
    byte[] downloadFromGoogleDrive(String fileId) throws IOException;
    
    // Local storage operations
    Path store(MultipartFile file) throws IOException;
    Path load(String filename);
    void delete(String filename) throws IOException;
    List<String> listAll();
    
    // Backup operations
    void createBackup(String directory) throws IOException;
    void restoreBackup(String backupFile) throws IOException;
} 