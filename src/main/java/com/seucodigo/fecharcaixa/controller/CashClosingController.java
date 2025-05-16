package com.seucodigo.fecharcaixa.controller;

import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.service.CashClosingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cash-closings")
@RequiredArgsConstructor
public class CashClosingController {

    private final CashClosingService cashClosingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> createCashClosing(
            @Valid @RequestBody CashClosing cashClosing,
            Authentication authentication) {
        cashClosing.setUserId(authentication.getName());
        return ResponseEntity.ok(cashClosingService.createCashClosing(cashClosing));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> updateCashClosing(
            @PathVariable String id,
            @Valid @RequestBody CashClosing cashClosing) {
        return ResponseEntity.ok(cashClosingService.updateCashClosing(id, cashClosing));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCashClosing(@PathVariable String id) {
        cashClosingService.deleteCashClosing(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> getCashClosing(@PathVariable String id) {
        return ResponseEntity.ok(cashClosingService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(cashClosingService.findByUserId(userId));
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(cashClosingService.findByPeriod(inicio, fim));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getPendingCashClosings() {
        return ResponseEntity.ok(cashClosingService.findPendingConference());
    }

    @PutMapping("/{id}/conferir")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosing> conferirCashClosing(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(cashClosingService.conferirFechamento(id, authentication.getName()));
    }

    @GetMapping("/user/{userId}/period")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<List<CashClosing>> getCashClosingsByUserAndPeriod(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(cashClosingService.findByUserIdAndPeriod(userId, inicio, fim));
    }
}