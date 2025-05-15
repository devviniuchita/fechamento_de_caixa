package com.seucodigo.fecharcaixa.repository;

import com.seucodigo.fecharcaixa.config.TestMongoConfig;
import com.seucodigo.fecharcaixa.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(User.Role.ADMIN);
        testUser.setActive(true);
        testUser.setName("Test User");

        userRepository.save(testUser);
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        var found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        var found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
    }

    @Test
    void whenExistsByUsername_thenReturnTrue() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
} 