package com.controle.fechamentocaixa.service.impl;

import com.controle.fechamentocaixa.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {
    
    @Override
    public String uploadToGoogleDrive(MultipartFile file) throws IOException {
        // Implementation will be provided by GoogleDriveService
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteFromGoogleDrive(String fileId) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] downloadFromGoogleDrive(String fileId) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Path store(MultipartFile file) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Path load(String filename) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(String filename) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<String> listAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void createBackup(String directory) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void restoreBackup(String backupFile) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 