package com.seucodigo.fecharcaixa.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Classe principal da aplicação JavaFX.
 * Responsável por inicializar o contexto Spring e carregar a interface gráfica.
 */
public class MainApp extends Application {
    
    private ConfigurableApplicationContext springContext;
    
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(FecharcaixaApplication.class)
            .run();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Sistema de Fechamento de Caixa");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        springContext.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 