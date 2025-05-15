package com.seucodigo.fecharcaixa.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestMongoConfig.class})
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void whenPasswordEncoderCreated_thenNotNull() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void whenAuthenticationManagerCreated_thenNotNull() {
        assertNotNull(authenticationManager);
    }

    @Test
    void whenEncodePassword_thenPasswordIsEncoded() {
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void whenAuthenticateWithValidCredentials_thenSuccess() {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        assertTrue(auth.isAuthenticated());
        assertEquals("testuser", auth.getName());
    }
} 