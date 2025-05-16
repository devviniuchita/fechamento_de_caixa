package com.controle.fechamentocaixa.service;

import com.controle.fechamentocaixa.dto.CashClosingDTO;
import com.controle.fechamentocaixa.model.CashClosing;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CashClosingService {
    CashClosing createCashClosing(CashClosingDTO dto, String userId);
    CashClosing updateCashClosing(String id, CashClosingDTO dto);
    void deleteCashClosing(String id);
    CashClosing getCashClosingById(String id);
    List<CashClosing> getCashClosingsByDateRange(LocalDate startDate, LocalDate endDate);
    List<CashClosing> getCashClosingsByResponsible(String responsibleId);
    List<CashClosing> getCashClosingsByResponsibleAndPeriod(String responsibleId, LocalDate startDate, LocalDate endDate);
    String uploadReceiptImage(String cashClosingId, MultipartFile file);
    byte[] generateExcelReport(LocalDate startDate, LocalDate endDate);
} 