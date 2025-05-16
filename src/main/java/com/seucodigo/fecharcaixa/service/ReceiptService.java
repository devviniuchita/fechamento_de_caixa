package com.seucodigo.fecharcaixa.service;

import com.google.api.services.drive.Drive;
import com.seucodigo.fecharcaixa.model.Receipt;
import com.seucodigo.fecharcaixa.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final Drive driveService;

    public Receipt createReceipt(Receipt receipt, MultipartFile file) throws IOException {
        receipt.setDataHora(LocalDateTime.now());
        
        if (file != null && !file.isEmpty()) {
            receipt.setNomeArquivo(file.getOriginalFilename());
            receipt.setContentType(file.getContentType());
            // TODO: Upload para Google Drive e setar URL
        }
        
        return receiptRepository.save(receipt);
    }

    public Receipt updateReceipt(String id, Receipt receipt, MultipartFile file) throws IOException {
        Receipt existingReceipt = findById(id);
        
        receipt.setId(id);
        receipt.setDataHora(existingReceipt.getDataHora());
        receipt.setCashClosingId(existingReceipt.getCashClosingId());
        
        if (file != null && !file.isEmpty()) {
            receipt.setNomeArquivo(file.getOriginalFilename());
            receipt.setContentType(file.getContentType());
            // TODO: Upload para Google Drive e setar URL
        } else {
            receipt.setNomeArquivo(existingReceipt.getNomeArquivo());
            receipt.setContentType(existingReceipt.getContentType());
            receipt.setUrlArquivo(existingReceipt.getUrlArquivo());
        }
        
        return receiptRepository.save(receipt);
    }

    public void deleteReceipt(String id) {
        Receipt receipt = findById(id);
        if (receipt.getUrlArquivo() != null) {
            // TODO: Deletar arquivo do Google Drive
        }
        receiptRepository.deleteById(id);
    }

    public Receipt findById(String id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));
    }

    public List<Receipt> findByCashClosingId(String cashClosingId) {
        return receiptRepository.findByCashClosingId(cashClosingId);
    }

    public List<Receipt> findByPeriod(LocalDateTime inicio, LocalDateTime fim) {
        return receiptRepository.findByDataHoraBetween(inicio, fim);
    }

    public List<Receipt> findByTipo(String tipo) {
        return receiptRepository.findByTipo(tipo);
    }

    public List<Receipt> findByCashClosingIdAndTipo(String cashClosingId, String tipo) {
        return receiptRepository.findByCashClosingIdAndTipo(cashClosingId, tipo);
    }

    private String uploadToDrive(MultipartFile file) throws IOException {
        // TODO: Implementar upload para o Google Drive
        return "drive-file-id";
    }
} 