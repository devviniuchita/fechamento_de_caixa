package com.seucodigo.fecharcaixa.service;

import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BackupService {
    private final Drive driveService;

    public String uploadReceipt(MultipartFile file) {
        // Implementar upload para Google Drive
        return "drive_file_id";
    }

    public void backupCashClosing(String cashClosingId) {
        // Implementar backup do fechamento
    }
} 