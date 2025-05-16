package com.controle.fechamentocaixa.service.impl;

import com.controle.fechamentocaixa.dto.ReceiptDTO;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.Receipt;
import com.controle.fechamentocaixa.repository.ReceiptRepository;
import com.controle.fechamentocaixa.service.GoogleDriveService;
import com.controle.fechamentocaixa.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final GoogleDriveService googleDriveService;

    @Override
    @Transactional
    public Receipt createReceipt(ReceiptDTO dto, MultipartFile file) {
        Receipt receipt = new Receipt();
        updateReceiptFromDTO(receipt, dto);
        receipt.setCreatedAt(LocalDateTime.now());
        
        if (file != null && !file.isEmpty()) {
            String fileId = uploadReceiptFile(receipt.getId(), file);
            receipt.setDriveFileUrl(fileId);
            receipt.setFileName(file.getOriginalFilename());
            receipt.setContentType(file.getContentType());
        }
        
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public Receipt updateReceipt(String id, ReceiptDTO dto, MultipartFile file) {
        Receipt receipt = findById(id);
        updateReceiptFromDTO(receipt, dto);
        
        if (file != null && !file.isEmpty()) {
            String fileId = uploadReceiptFile(receipt.getId(), file);
            receipt.setDriveFileUrl(fileId);
            receipt.setFileName(file.getOriginalFilename());
            receipt.setContentType(file.getContentType());
        }
        
        return receiptRepository.save(receipt);
    }

    @Override
    @Transactional
    public void deleteReceipt(String id) {
        Receipt receipt = findById(id);
        
        if (receipt.getDriveFileUrl() != null) {
            try {
                googleDriveService.deleteFile(receipt.getDriveFileUrl());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao excluir arquivo do Google Drive", e);
            }
        }
        
        receiptRepository.deleteById(id);
    }

    @Override
    public Receipt findById(String id) {
        return receiptRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comprovante não encontrado com id: " + id));
    }

    @Override
    public List<Receipt> findByCashClosingId(String cashClosingId) {
        return receiptRepository.findByCashClosingId(cashClosingId);
    }

    @Override
    public List<Receipt> findByPeriod(LocalDateTime start, LocalDateTime end) {
        return receiptRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<Receipt> findByPaymentType(String paymentType) {
        return receiptRepository.findByPaymentType(paymentType);
    }

    @Override
    public List<Receipt> findByCashClosingIdAndPaymentType(String cashClosingId, String paymentType) {
        return receiptRepository.findByCashClosingIdAndPaymentType(cashClosingId, paymentType);
    }

    @Override
    public String uploadReceiptFile(String receiptId, MultipartFile file) {
        try {
            return googleDriveService.uploadFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload do arquivo", e);
        }
    }

    private void updateReceiptFromDTO(Receipt receipt, ReceiptDTO dto) {
        receipt.setPaymentType(dto.getPaymentType());
        receipt.setAmount(dto.getAmount());
        receipt.setTransactionNumber(dto.getTransactionNumber());
        receipt.setCardBrand(dto.getCardBrand());
        receipt.setLastFourDigits(dto.getLastFourDigits());
        receipt.setCashClosingId(dto.getCashClosingId());
        receipt.setNotes(dto.getNotes());
    }
} 