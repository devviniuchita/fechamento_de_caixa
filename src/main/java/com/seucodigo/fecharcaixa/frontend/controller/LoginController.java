package com.seucodigo.fecharcaixa.frontend.controller;

import com.seucodigo.fecharcaixa.frontend.service.AuthService;
import com.seucodigo.fecharcaixa.frontend.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Controller da tela de login.
 * Gerencia a autenticação do usuário na interface desktop.
 */
@Component
@RequiredArgsConstructor
public class LoginController {
    
    private final AuthService authService;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    public void initialize() {
        // Configurações iniciais da tela
    }
    
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        try {
            authService.login(username, password);
            // Navegar para tela principal
        } catch (Exception e) {
            AlertUtils.showError("Erro de Login", "Não foi possível fazer login", 
                e.getMessage());
        }
    }
    
    @FXML
    public void handleForgotPassword() {
        // Implementar recuperação de senha
    }
} 