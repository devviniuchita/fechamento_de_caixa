package com.controle.fechamentocaixa.frontend.controller;

import com.controle.fechamentocaixa.frontend.service.AuthService;
import com.controle.fechamentocaixa.frontend.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

/**
 * Controller da tela de login.
 * Gerencia a autenticação do usuário na interface desktop.
 */
@Slf4j
@Controller
public class LoginController {
    
    private final AuthService authService;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    public LoginController(AuthService authService) {
        this.authService = authService;
    }
    
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
            log.warn("Tentativa de login com campos vazios");
            AlertUtils.showWarning("Campos Vazios", "Por favor, preencha todos os campos", "Username e senha são obrigatórios");
            return;
        }
        
        try {
            boolean success = authService.authenticate(username, password);
            if (success) {
                log.info("Login bem sucedido para usuário: {}", username);
                // Navegar para a próxima tela
            } else {
                log.warn("Falha na autenticação para usuário: {}", username);
                log.debug("Credenciais inválidas");
                AlertUtils.showError("Erro de Login", "Credenciais Inválidas", "Username ou senha incorretos");
            }
        } catch (Exception e) {
            log.error("Erro durante o login", e);
            AlertUtils.showInfo("Erro", "Erro no Sistema", "Ocorreu um erro ao processar o login");
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