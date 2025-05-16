package com.controle.fechamentocaixa.service;

import com.controle.fechamentocaixa.dto.ReceiptDTO;
import com.controle.fechamentocaixa.model.Receipt;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptService {
    Receipt createReceipt(ReceiptDTO dto, MultipartFile file);
    Receipt updateReceipt(String id, ReceiptDTO dto, MultipartFile file);
    void deleteReceipt(String id);
    Receipt findById(String id);
    List<Receipt> findByCashClosingId(String cashClosingId);
    List<Receipt> findByPeriod(LocalDateTime start, LocalDateTime end);
    List<Receipt> findByPaymentType(String paymentType);
    List<Receipt> findByCashClosingIdAndPaymentType(String cashClosingId, String paymentType);
    String uploadReceiptFile(String receiptId, MultipartFile file);
} 