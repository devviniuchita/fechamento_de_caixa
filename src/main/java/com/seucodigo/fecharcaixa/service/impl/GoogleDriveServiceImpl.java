package com.seucodigo.fecharcaixa.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.service.GoogleDriveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    @Value("${google.drive.credentials-folder}")
    private String credentialsFolder;

    @Value("${google.drive.tokens-folder}")
    private String tokensFolder;

    @Value("${google.drive.application-name}")
    private String applicationName;

    @Value("${google.drive.backup-folder-id}")
    private String backupFolderId;

    private Drive driveService;

    public GoogleDriveServiceImpl() {
        try {
            initializeDriveService();
        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to initialize Google Drive service", e);
        }
    }

    private void initializeDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(applicationName)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets
        InputStream in = new FileInputStream(new java.io.File(credentialsFolder + "/credentials.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensFolder)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(path.substring(path.lastIndexOf('/') + 1));
        fileMetadata.setParents(Collections.singletonList(backupFolderId));

        File uploadedFile = driveService.files().create(fileMetadata,
                new InputStreamContent(file.getContentType(), file.getInputStream()))
                .setFields("id")
                .execute();

        return uploadedFile.getId();
    }

    @Override
    public byte[] downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    @Override
    public String backupCashClosing(CashClosing cashClosing) throws IOException {
        // Create JSON representation of cash closing
        String json = convertCashClosingToJson(cashClosing);
        
        // Create file metadata
        File fileMetadata = new File();
        fileMetadata.setName(String.format("cash_closing_%s_%s.json",
                cashClosing.getDate(),
                cashClosing.getId()));
        fileMetadata.setParents(Collections.singletonList(backupFolderId));

        // Upload file content
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        File uploadedFile = driveService.files().create(fileMetadata,
                new InputStreamContent("application/json", inputStream))
                .setFields("id")
                .execute();

        return uploadedFile.getId();
    }

    @Override
    @Scheduled(cron = "0 0 23 * * *") // Run at 23:00 every day
    public void scheduleBackup() {
        try {
            // Create backup folder for today if it doesn't exist
            String todayFolder = createDailyBackupFolder();

            // Search for files in backup folder
            FileList result = driveService.files().list()
                    .setQ(String.format("'%s' in parents", backupFolderId))
                    .setFields("files(id, name)")
                    .execute();

            // Move files to today's folder
            for (File file : result.getFiles()) {
                if (!file.getId().equals(todayFolder)) {
                    file.setParents(Collections.singletonList(todayFolder));
                    driveService.files().update(file.getId(), file)
                            .setAddParents(todayFolder)
                            .setRemoveParents(backupFolderId)
                            .execute();
                }
            }
        } catch (IOException e) {
            log.error("Failed to perform scheduled backup", e);
        }
    }

    private String createDailyBackupFolder() throws IOException {
        String folderName = "backup_" + LocalDate.now();
        
        // Check if folder already exists
        FileList result = driveService.files().list()
                .setQ(String.format("name='%s' and mimeType='application/vnd.google-apps.folder'", folderName))
                .setSpaces("drive")
                .execute();

        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        // Create new folder
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        folderMetadata.setParents(Collections.singletonList(backupFolderId));

        File folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    private String convertCashClosingToJson(CashClosing cashClosing) {
        // Implement JSON conversion using Jackson or Gson
        // This is a simplified version
        return String.format("""
            {
                "id": "%s",
                "date": "%s",
                "responsibleId": "%s",
                "responsibleName": "%s",
                "totalIncome": %s,
                "totalAssets": %s,
                "hasInconsistency": %s
            }
            """,
            cashClosing.getId(),
            cashClosing.getDate(),
            cashClosing.getResponsibleId(),
            cashClosing.getResponsibleName(),
            cashClosing.getTotalIncome(),
            cashClosing.getTotalAssets(),
            cashClosing.isHasInconsistency()
        );
    }
} 