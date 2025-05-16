package com.controle.fechamentocaixa.integration;

import com.controle.fechamentocaixa.model.User;
import com.controle.fechamentocaixa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        // Given
        User user = new User("testuser", "password", "Test User", "test@example.com", 
            Arrays.asList("CAIXA"));

        // When
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser)
            .isPresent()
            .get()
            .satisfies(u -> {
                assertThat(u.getId()).isNotNull();
                assertThat(u.getUsername()).isEqualTo("testuser");
                assertThat(u.getNome()).isEqualTo("Test User");
                assertThat(u.getEmail()).isEqualTo("test@example.com");
                assertThat(u.getRoles()).containsExactly("CAIXA");
            });
    }

    @Test
    void shouldCheckExistsByUsername() {
        // Given
        User user = new User("existinguser", "password", "Existing User", "existing@example.com", 
            Arrays.asList("CAIXA"));
        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByUsername("existinguser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistinguser")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        // Given
        User user = new User("emailuser", "password", "Email User", "email@example.com", 
            Arrays.asList("CAIXA"));
        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByEmail("email@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexisting@example.com")).isFalse();
    }
} 