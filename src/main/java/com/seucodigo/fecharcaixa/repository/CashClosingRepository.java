package com.seucodigo.fecharcaixa.repository;

import com.seucodigo.fecharcaixa.model.CashClosing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    List<CashClosing> findByUserId(String userId);
    List<CashClosing> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<CashClosing> findByConferidoFalse();
    List<CashClosing> findByUserIdAndDataHoraBetween(String userId, LocalDateTime inicio, LocalDateTime fim);
} 