package com.seucodigo.fecharcaixa.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKey12345678901234567890testSecretKey");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 3600000L);
        jwtTokenProvider.init();
    }

    @Test
    void whenGenerateToken_thenTokenIsValid() {
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void whenValidateToken_withInvalidToken_thenReturnFalse() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void whenGetAuthentication_thenAuthenticationIsValid() {
        String username = "testuser";
        UserDetails userDetails = new User(username, "", new ArrayList<>());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        String token = jwtTokenProvider.generateToken(username);
        Authentication auth = jwtTokenProvider.getAuthentication(token);

        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(username, auth.getName());
    }
} 