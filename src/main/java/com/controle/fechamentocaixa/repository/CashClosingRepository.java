package com.controle.fechamentocaixa.repository;

import com.controle.fechamentocaixa.model.CashClosing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    List<CashClosing> findByResponsibleId(String responsibleId);
    List<CashClosing> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
    List<CashClosing> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
    List<CashClosing> findByResponsibleIdAndCreatedAtBetween(String responsibleId, LocalDate startDate, LocalDate endDate);
} 