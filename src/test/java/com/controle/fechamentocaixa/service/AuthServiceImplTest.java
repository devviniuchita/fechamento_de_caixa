package com.controle.fechamentocaixa.service;

import com.controle.fechamentocaixa.frontend.service.impl.AuthServiceImpl;
import com.controle.fechamentocaixa.model.User;
import com.controle.fechamentocaixa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(authenticationManager, userRepository);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        String password = "password";
        User expectedUser = new User(username, password, "Test User", "test@example.com", null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsername(username))
            .thenReturn(Optional.of(expectedUser));

        // Act
        User result = authService.login(username, password);

        // Assert
        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedUser);
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, password))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Falha na autenticação");
    }
} 