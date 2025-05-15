package com.seucodigo.fecharcaixa.integration;

import com.seucodigo.fecharcaixa.config.JwtTokenProvider;
import com.seucodigo.fecharcaixa.config.TestMongoConfig;
import com.seucodigo.fecharcaixa.model.User;
import com.seucodigo.fecharcaixa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
class SecurityIntegrationTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String rawPassword;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        rawPassword = "testPassword123";
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode(rawPassword));
        testUser.setRole(User.Role.ADMIN);
        testUser.setActive(true);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        
        userRepository.save(testUser);
    }

    @Test
    void whenAuthenticateAndGenerateToken_thenSuccess() {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(testUser.getUsername(), rawPassword)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate token
        String token = jwtTokenProvider.generateToken(testUser.getUsername());
        assertNotNull(token);

        // Validate token
        assertTrue(jwtTokenProvider.validateToken(token));

        // Get authentication from token
        Authentication tokenAuth = jwtTokenProvider.getAuthentication(token);
        assertNotNull(tokenAuth);
        assertEquals(testUser.getUsername(), tokenAuth.getName());
    }

    @Test
    void whenValidateTokenWithInvalidSignature_thenFail() {
        String invalidToken = "***REMOVED***" +
                            "eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjE2MjAw" +
                            "MCwiZXhwIjoxNjE2MTY1NjAwfQ." +
                            "invalid_signature";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
} 