package com.controle.fechamentocaixa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

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