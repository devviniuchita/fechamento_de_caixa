package com.controle.fechamentocaixa.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface GoogleDriveService {
    String uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String fileId) throws IOException;
    byte[] downloadFile(String fileId) throws IOException;
} 