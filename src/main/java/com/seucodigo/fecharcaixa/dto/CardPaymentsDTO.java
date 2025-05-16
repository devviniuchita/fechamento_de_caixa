package com.seucodigo.fecharcaixa.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CardPaymentsDTO {
    @NotNull
    @PositiveOrZero
    private Double visa;

    @NotNull
    @PositiveOrZero
    private Double mastercard;

    @NotNull
    @PositiveOrZero
    private Double elo;
} 