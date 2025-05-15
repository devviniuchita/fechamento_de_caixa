package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.dto.CashClosingDTO;
import com.seucodigo.fecharcaixa.model.CashClosing;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CashClosingService {
    CashClosing createCashClosing(CashClosingDTO cashClosingDTO, String userId);
    CashClosing updateCashClosing(String id, CashClosingDTO cashClosingDTO);
    void deleteCashClosing(String id);
    CashClosing getCashClosingById(String id);
    List<CashClosing> getCashClosingsByDateRange(LocalDate startDate, LocalDate endDate);
    List<CashClosing> getCashClosingsByDate(LocalDate date);
    List<CashClosing> getCashClosingsByResponsible(String responsibleId);
    void uploadReceipt(String id, MultipartFile file);
    byte[] generateDailyReport(LocalDate date);
    byte[] generateWeeklyReport(LocalDate startDate);
    byte[] generateMonthlyReport(LocalDate month);
    void backupToGoogleDrive(String cashClosingId);
} 