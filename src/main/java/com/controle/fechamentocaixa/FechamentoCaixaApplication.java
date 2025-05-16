package com.controle.fechamentocaixa;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe principal da aplicação que integra Spring Boot com JavaFX.
 * Responsável por inicializar o contexto Spring e carregar a interface gráfica.
 */
@SpringBootApplication
@Slf4j
public class FechamentoCaixaApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(FechamentoCaixaApplication.class)
            .headless(false)
            .run();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Sistema de Fechamento de Caixa");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            log.info("Aplicação JavaFX iniciada com sucesso");
        } catch (Exception e) {
            log.error("Erro ao iniciar a aplicação JavaFX", e);
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
        log.info("Aplicação encerrada");
    }
} 