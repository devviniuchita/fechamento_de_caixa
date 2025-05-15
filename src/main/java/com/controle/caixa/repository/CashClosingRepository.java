package com.controle.caixa.repository;

import com.controle.caixa.model.CashClosing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    List<CashClosing> findByClosingDate(LocalDate date);
    List<CashClosing> findByClosingDateBetween(LocalDate startDate, LocalDate endDate);
} 