package com.controle.fechamentocaixa.controller;

import com.controle.fechamentocaixa.frontend.controller.LoginController;
import com.controle.fechamentocaixa.frontend.service.AuthService;
import com.controle.fechamentocaixa.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.assertions.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

    @Mock
    private AuthService authService;

    @Autowired
    private ApplicationContext applicationContext;

    private LoginController loginController;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/view/login.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        loginController = loader.getController();
        
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void shouldShowErrorOnEmptyCredentials(FxRobot robot) {
        // When
        robot.clickOn("#loginButton");

        // Then
        assertThat(robot.lookup(".alert").queryAs(javafx.scene.control.Alert.class))
            .isNotNull()
            .satisfies(alert -> {
                assertThat(alert.getContentText()).contains("Por favor, preencha usuário e senha");
            });
    }

    @Test
    void shouldLoginSuccessfully(FxRobot robot) {
        // Given
        User user = new User("testuser", "password", "Test User", "test@example.com", null);
        when(authService.login(anyString(), anyString())).thenReturn(user);

        // When
        robot.clickOn("#usernameField").write("testuser");
        robot.clickOn("#passwordField").write("password");
        robot.clickOn("#loginButton");

        // Then
        // TODO: Add assertions for successful login navigation
    }
} 