package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.model.CashClosing;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GoogleDriveService {
    String uploadFile(MultipartFile file, String path) throws IOException;
    byte[] downloadFile(String fileId) throws IOException;
    void deleteFile(String fileId) throws IOException;
    String backupCashClosing(CashClosing cashClosing) throws IOException;
    void scheduleBackup();
} 