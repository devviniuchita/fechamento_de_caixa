package com.controle.fechamentocaixa.repository;

import com.controle.fechamentocaixa.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    List<Receipt> findByCashClosingId(String cashClosingId);
    List<Receipt> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Receipt> findByPaymentType(String paymentType);
    List<Receipt> findByCashClosingIdAndPaymentType(String cashClosingId, String paymentType);
} 