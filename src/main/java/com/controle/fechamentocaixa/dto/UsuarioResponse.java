package com.controle.fechamentocaixa.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta com dados de usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    
    private String id;
    private String nome;
    private String email;
    private List<String> perfis;
    private boolean ativo;
    private LocalDateTime dataCriacao;
} 