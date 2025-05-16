package com.controle.fechamentocaixa.frontend.controller;

import com.controle.fechamentocaixa.frontend.service.AuthService;
import com.controle.fechamentocaixa.frontend.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller da tela de login.
 * Gerencia a autenticação do usuário na interface desktop.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    
    private final AuthService authService;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    public void initialize() {
        log.debug("Inicializando tela de login");
        // Configurações iniciais da tela, se necessário
    }
    
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showWarning("Validação", "Campos obrigatórios", 
                "Por favor, preencha usuário e senha.");
            return;
        }
        
        try {
            log.debug("Tentando autenticar usuário: {}", username);
            authService.login(username, password);
            // TODO: Navegar para tela principal após implementar AuthService
            log.info("Usuário {} autenticado com sucesso", username);
        } catch (Exception e) {
            log.error("Erro ao realizar login", e);
            AlertUtils.showError("Erro de Login", "Não foi possível fazer login", 
                e.getMessage());
        }
    }
    
    @FXML
    public void handleForgotPassword() {
        log.debug("Iniciando processo de recuperação de senha");
        AlertUtils.showInfo("Recuperação de Senha", 
            "Funcionalidade em desenvolvimento", 
            "Entre em contato com o administrador do sistema.");
    }
} 