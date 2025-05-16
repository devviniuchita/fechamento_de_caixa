package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.repository.CashClosingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashClosingService {

    private final CashClosingRepository cashClosingRepository;

    public CashClosing createCashClosing(CashClosing cashClosing) {
        cashClosing.setDataHora(LocalDateTime.now());
        return cashClosingRepository.save(cashClosing);
    }

    public CashClosing updateCashClosing(String id, CashClosing cashClosing) {
        CashClosing existingCashClosing = findById(id);
        
        // Não permite alterar dados básicos após criação
        cashClosing.setId(id);
        cashClosing.setDataHora(existingCashClosing.getDataHora());
        cashClosing.setUserId(existingCashClosing.getUserId());
        
        return cashClosingRepository.save(cashClosing);
    }

    public void deleteCashClosing(String id) {
        cashClosingRepository.deleteById(id);
    }

    public CashClosing findById(String id) {
        return cashClosingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fechamento de caixa não encontrado"));
    }

    public List<CashClosing> findByUserId(String userId) {
        return cashClosingRepository.findByUserId(userId);
    }

    public List<CashClosing> findByPeriod(LocalDateTime inicio, LocalDateTime fim) {
        return cashClosingRepository.findByDataHoraBetween(inicio, fim);
    }

    public List<CashClosing> findPendingConference() {
        return cashClosingRepository.findByConferidoFalse();
    }

    public CashClosing conferirFechamento(String id, String conferidoPor) {
        CashClosing cashClosing = findById(id);
        cashClosing.setConferido(true);
        cashClosing.setConferidoPor(conferidoPor);
        cashClosing.setDataConferencia(LocalDateTime.now());
        return cashClosingRepository.save(cashClosing);
    }

    public List<CashClosing> findByUserIdAndPeriod(String userId, LocalDateTime inicio, LocalDateTime fim) {
        return cashClosingRepository.findByUserIdAndDataHoraBetween(userId, inicio, fim);
    }
} 