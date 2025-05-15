package com.seucodigo.fecharcaixa.controller;

import com.seucodigo.fecharcaixa.dto.CashClosingDTO;
import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.service.CashClosingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cash-closings")
@RequiredArgsConstructor
public class CashClosingController {

    private final CashClosingService cashClosingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> createCashClosing(
            @Valid @RequestBody CashClosingDTO cashClosingDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cashClosingService.createCashClosing(cashClosingDTO, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> updateCashClosing(
            @PathVariable String id,
            @Valid @RequestBody CashClosingDTO cashClosingDTO) {
        return ResponseEntity.ok(cashClosingService.updateCashClosing(id, cashClosingDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCashClosing(@PathVariable String id) {
        cashClosingService.deleteCashClosing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> getCashClosingById(@PathVariable String id) {
        return ResponseEntity.ok(cashClosingService.getCashClosingById(id));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashClosingService.getCashClosingsByDate(date));
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(cashClosingService.getCashClosingsByDateRange(startDate, endDate));
    }

    @GetMapping("/responsible/{responsibleId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByResponsible(
            @PathVariable String responsibleId) {
        return ResponseEntity.ok(cashClosingService.getCashClosingsByResponsible(responsibleId));
    }

    @PostMapping("/{id}/receipt")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<Void> uploadReceipt(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        cashClosingService.uploadReceipt(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/reports/daily/{date}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<byte[]> generateDailyReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=daily-report-" + date + ".xlsx")
                .body(cashClosingService.generateDailyReport(date));
    }

    @GetMapping(value = "/reports/weekly/{startDate}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<byte[]> generateWeeklyReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=weekly-report-" + startDate + ".xlsx")
                .body(cashClosingService.generateWeeklyReport(startDate));
    }

    @GetMapping(value = "/reports/monthly/{month}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<byte[]> generateMonthlyReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=monthly-report-" + month.getMonth() + ".xlsx")
                .body(cashClosingService.generateMonthlyReport(month));
    }

    @PostMapping("/{id}/backup")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<Void> backupToGoogleDrive(@PathVariable String id) {
        cashClosingService.backupToGoogleDrive(id);
        return ResponseEntity.ok().build();
    }
} 