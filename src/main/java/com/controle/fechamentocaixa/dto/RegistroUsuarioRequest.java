package com.controle.fechamentocaixa.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitação de registro de novo usuário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 40, message = "Senha deve ter entre 6 e 40 caracteres")
    private String senha;
    
    private Set<String> perfis;
} 