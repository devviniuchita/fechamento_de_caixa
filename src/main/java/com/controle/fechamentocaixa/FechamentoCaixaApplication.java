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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principal da aplicação que integra Spring Boot com JavaFX.
 * Responsável por inicializar o contexto Spring e carregar a interface gráfica.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.controle.fechamentocaixa")
@EnableMongoRepositories(basePackages = "com.controle.fechamentocaixa.repository")
@EntityScan(basePackages = "com.controle.fechamentocaixa.model")
public class FechamentoCaixaApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(FechamentoCaixaApplication.class);
    private ConfigurableApplicationContext springContext;
    private static String[] savedArgs;

    public static void main(String[] args) {
        savedArgs = args;
        launch(args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(FechamentoCaixaApplication.class)
            .headless(false)
            .run(savedArgs);
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
            
            logger.info("Aplicação JavaFX iniciada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao iniciar a aplicação JavaFX", e);
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
        logger.info("Aplicação encerrada");
    }
} 