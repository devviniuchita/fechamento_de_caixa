package com.controle.caixa.service;

import com.controle.caixa.model.CashClosing;
import com.controle.caixa.repository.CashClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CashClosingService {

    @Autowired
    private CashClosingRepository cashClosingRepository;

    public CashClosing save(CashClosing cashClosing) {
        validateCashClosing(cashClosing);
        return cashClosingRepository.save(cashClosing);
    }

    public Optional<CashClosing> findById(String id) {
        return cashClosingRepository.findById(id);
    }

    public List<CashClosing> findByDate(LocalDate date) {
        return cashClosingRepository.findByClosingDate(date);
    }

    public List<CashClosing> findByPeriod(LocalDate startDate, LocalDate endDate) {
        return cashClosingRepository.findByClosingDateBetween(startDate, endDate);
    }

    public Optional<CashClosing> update(String id, CashClosing cashClosing) {
        if (!cashClosingRepository.existsById(id)) {
            return Optional.empty();
        }
        validateCashClosing(cashClosing);
        cashClosing.setId(id);
        return Optional.of(cashClosingRepository.save(cashClosing));
    }

    public boolean delete(String id) {
        if (!cashClosingRepository.existsById(id)) {
            return false;
        }
        cashClosingRepository.deleteById(id);
        return true;
    }

    private void validateCashClosing(CashClosing cashClosing) {
        double totalPayments = cashClosing.getCash() +
                cashClosing.getPix() +
                cashClosing.getDeposit() +
                cashClosing.getDebitCards().getTotal() +
                cashClosing.getCreditCards().getTotal() +
                cashClosing.getVouchers();

        double expectedTotal = cashClosing.getInitialAmount() +
                cashClosing.getSales() +
                cashClosing.getChangeInserted() -
                cashClosing.getWithdrawal();

        if (Math.abs(totalPayments - expectedTotal) > 0.01) {
            throw new IllegalArgumentException("Total payments do not match expected total");
        }
    }
} 