package com.controle.fechamentocaixa.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Serviço para inicialização de usuários
 */
@Service
@Profile("userinit")
@Order(2) // Execute after DataMigrationService
public class UserInitService implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(UserInitService.class);

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    logger.info("Iniciando inicialização de usuários...");

    // Fix existing users with plain text passwords
    fixExistingUsers();

    // Create test admin user
    createTestAdminUser();

    logger.info("Inicialização de usuários concluída.");
  }

  private void fixExistingUsers() {
    try {
      usuarioRepository.findAll().forEach(user -> {
        String pwd = user.getSenha();
        if (pwd != null && !pwd.isBlank()) {
          boolean alreadyEncoded = pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$");
          if (!alreadyEncoded) {
            logger.info("Fixing password for user: {}", user.getEmail());
            user.setSenha(passwordEncoder.encode(user.getSenha()));
            usuarioRepository.save(user);
            logger.info("Password fixed for user: {}", user.getEmail());
          }
        }
      });
    } catch (Exception e) {
      logger.error("Error fixing user passwords: ", e);
    }
  }

  private void createTestAdminUser() {
    try {
      String testEmail = "admin@test.com";
      if (!usuarioRepository.existsByEmail(testEmail)) {
        Set<Perfil> perfis = new HashSet<>();
        perfis.add(Perfil.ADMIN);

        Usuario testAdmin = new Usuario();
        testAdmin.setEmail(testEmail);
        testAdmin.setNome("Test Admin");
        testAdmin.setSenha(passwordEncoder.encode("admin123"));
        testAdmin.setPerfis(perfis);
        testAdmin.setAtivo(true);
        testAdmin.setDataCriacao(LocalDateTime.now());

        usuarioRepository.save(testAdmin);
        logger.info("Test admin user created: {} / admin123", testEmail);
      }
    } catch (Exception e) {
      logger.error("Error creating test admin user: ", e);
    }
  }
}
