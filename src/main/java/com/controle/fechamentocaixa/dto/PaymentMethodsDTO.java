package com.controle.fechamentocaixa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentMethodsDTO {
    @NotNull
    @PositiveOrZero
    private BigDecimal cash = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal pix = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal deposit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal giftCard = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal withdrawal = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal visaDebit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal masterDebit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal eloDebit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal visaCredit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal masterCredit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal eloCredit = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal voucher = BigDecimal.ZERO;
} 