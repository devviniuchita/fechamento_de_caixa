package com.controle.fechamentocaixa.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login bem-sucedido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String tipo = "Bearer";
    private String id;
    private String nome;
    private String email;
    private List<String> perfis;
} 