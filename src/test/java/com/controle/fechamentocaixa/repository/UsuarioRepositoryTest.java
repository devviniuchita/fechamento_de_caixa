package com.controle.fechamentocaixa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;

@DataMongoTest
@EnabledIfEnvironmentVariable(named = "ENABLE_DOCKER_TESTS", matches = "true")
public class UsuarioRepositoryTest {

  static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0.8");

  @Autowired
  private UsuarioRepository usuarioRepository;

  private Usuario usuario;

  @BeforeAll
  static void startMongo() {
    if (!"true".equals(System.getenv("ENABLE_DOCKER_TESTS"))) {
      Assumptions.assumeTrue(false, "Docker-based tests desabilitados: defina ENABLE_DOCKER_TESTS=true para executar este teste.");
    }
    mongo.start();
  }

  @AfterAll
  static void stopMongo() {
    if (mongo != null) {
      mongo.stop();
    }
  }

  @org.springframework.test.context.DynamicPropertySource
  static void mongoProps(org.springframework.test.context.DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongo::getConnectionString);
  }

  @BeforeEach
  void setup() {
    // Limpa o repositório antes de cada teste
    usuarioRepository.deleteAll();

    // Configura um usuário para testes
    Set<Perfil> perfis = new HashSet<>();
    perfis.add(Perfil.CAIXA);

    usuario = new Usuario();
    usuario.setEmail("teste@example.com");
    usuario.setNome("Usuário Teste");
    usuario.setSenha("senha123");
    usuario.setPerfis(perfis);
    usuario.setAtivo(true);
    usuario.setDataCriacao(LocalDateTime.now());

    // Salva o usuário no repositório
    usuario = usuarioRepository.save(usuario);
  }

  @Test
  @DisplayName("Deve encontrar usuário pelo email")
  void deveEncontrarUsuarioPeloEmail() {
    // Arrange - já feito no setup

    // Act
    Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("teste@example.com");

    // Assert
    assertThat(usuarioEncontrado).isPresent();
    assertThat(usuarioEncontrado.get().getEmail()).isEqualTo("teste@example.com");
    assertThat(usuarioEncontrado.get().getNome()).isEqualTo("Usuário Teste");
  }

  @Test
  @DisplayName("Não deve encontrar usuário com email inexistente")
  void naoDeveEncontrarUsuarioComEmailInexistente() {
    // Act
    Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("inexistente@example.com");

    // Assert
    assertThat(usuarioEncontrado).isEmpty();
  }

  @Test
  @DisplayName("Deve verificar existência de usuário pelo email")
  void deveVerificarExistenciaDeUsuarioPeloEmail() {
    // Act & Assert
    assertThat(usuarioRepository.existsByEmail("teste@example.com")).isTrue();
    assertThat(usuarioRepository.existsByEmail("inexistente@example.com")).isFalse();
  }
}
