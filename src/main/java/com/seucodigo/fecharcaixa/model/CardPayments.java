package com.seucodigo.fecharcaixa.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class CardPayments {
    private Double visa;
    private Double mastercard;
    private Double elo;

    public Double getTotal() {
        return (visa != null ? visa : 0.0) +
               (mastercard != null ? mastercard : 0.0) +
               (elo != null ? elo : 0.0);
    }
} 