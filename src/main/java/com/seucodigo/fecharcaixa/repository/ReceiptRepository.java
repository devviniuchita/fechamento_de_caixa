package com.seucodigo.fecharcaixa.repository;

import com.seucodigo.fecharcaixa.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    List<Receipt> findByCashClosingId(String cashClosingId);
    List<Receipt> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Receipt> findByTipo(String tipo);
    List<Receipt> findByCashClosingIdAndTipo(String cashClosingId, String tipo);
} 