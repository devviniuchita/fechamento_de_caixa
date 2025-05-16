package com.seucodigo.fecharcaixa.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
} 