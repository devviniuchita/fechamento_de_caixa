package com.controle.fechamentocaixa.frontend;

import javafx.stage.Stage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Configuration
public class FrontendConfig {
    
    private Stage primaryStage;
    
    @Bean
    @Lazy
    public Stage primaryStage() {
        if (primaryStage == null) {
            primaryStage = new Stage();
        }
        return primaryStage;
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
} 