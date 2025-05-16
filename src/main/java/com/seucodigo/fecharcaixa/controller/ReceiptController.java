package com.seucodigo.fecharcaixa.controller;

import com.seucodigo.fecharcaixa.model.Receipt;
import com.seucodigo.fecharcaixa.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<Receipt> createReceipt(
            @Valid @RequestPart("receipt") Receipt receipt,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(receiptService.createReceipt(receipt, file));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<Receipt> updateReceipt(
            @PathVariable String id,
            @Valid @RequestPart("receipt") Receipt receipt,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(receiptService.updateReceipt(id, receipt, file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<Void> deleteReceipt(@PathVariable String id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<Receipt> getReceipt(@PathVariable String id) {
        return ResponseEntity.ok(receiptService.findById(id));
    }

    @GetMapping("/cash-closing/{cashClosingId}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<List<Receipt>> getReceiptsByCashClosing(@PathVariable String cashClosingId) {
        return ResponseEntity.ok(receiptService.findByCashClosingId(cashClosingId));
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<Receipt>> getReceiptsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(receiptService.findByPeriod(inicio, fim));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<Receipt>> getReceiptsByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(receiptService.findByTipo(tipo));
    }

    @GetMapping("/cash-closing/{cashClosingId}/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<List<Receipt>> getReceiptsByCashClosingAndTipo(
            @PathVariable String cashClosingId,
            @PathVariable String tipo) {
        return ResponseEntity.ok(receiptService.findByCashClosingIdAndTipo(cashClosingId, tipo));
    }
} 