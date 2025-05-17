package com.controle.fechamentocaixa.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de usuário para autenticação e autorização no sistema
 */
@Document(collection = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String nome;
    
    private String senha; // Armazenada com BCrypt
    
    private Set<Perfil> perfis = new HashSet<>();
    
    private boolean ativo = true;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataUltimoAcesso;
    
    public boolean possuiPerfil(Perfil perfil) {
        return perfis.contains(perfil);
    }
    
    public boolean isAdmin() {
        return perfis.contains(Perfil.ADMIN);
    }
} 