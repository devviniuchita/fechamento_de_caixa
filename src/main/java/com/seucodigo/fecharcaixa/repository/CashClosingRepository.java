package com.seucodigo.fecharcaixa.repository;

import com.seucodigo.fecharcaixa.model.CashClosing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    List<CashClosing> findByDateOrderByCreatedAtDesc(LocalDate date);
    List<CashClosing> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
    List<CashClosing> findByResponsibleIdOrderByDateDesc(String responsibleId);
    boolean existsByDate(LocalDate date);
} 