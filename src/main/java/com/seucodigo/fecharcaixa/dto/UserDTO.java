package com.seucodigo.fecharcaixa.dto;

import com.seucodigo.fecharcaixa.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private String password;
    
    @NotNull(message = "Role is required")
    private User.Role role;
    
    private boolean active = true;
} 