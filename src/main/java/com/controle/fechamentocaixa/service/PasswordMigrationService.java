package com.controle.fechamentocaixa.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Service responsible for migrating plain text passwords to BCrypt encoding
 */
@Service
public class PasswordMigrationService {

  private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationService.class);

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Migrates all users with plain text passwords to BCrypt encoding
   */
  public void migrateAllPasswords() {
    try {
      logger.info("Starting password migration process...");

      List<Usuario> allUsers = usuarioRepository.findAll();
      int totalUsers = allUsers.size();
      int migratedCount = 0;
      int skippedCount = 0;
      int errorCount = 0;

      logger.info("Found {} users to process", totalUsers);

      for (Usuario user : allUsers) {
        try {
          if (isPasswordEncoded(user.getSenha())) {
            logger.debug("Skipping user {} - password already encoded", user.getEmail());
            skippedCount++;
            continue;
          }

          migrateUserPassword(user);
          migratedCount++;
          logger.info("Successfully migrated password for user: {}", user.getEmail());

        } catch (Exception e) {
          errorCount++;
          logger.error("Failed to migrate password for user: {} - Error: {}",
              user.getEmail(), e.getMessage(), e);
        }
      }

      logger.info("Password migration completed. Total: {}, Migrated: {}, Skipped: {}, Errors: {}",
          totalUsers, migratedCount, skippedCount, errorCount);

    } catch (Exception e) {
      logger.error("Critical error during password migration: {}", e.getMessage(), e);
      throw new RuntimeException("Password migration failed", e);
    }
  }

  /**
   * Checks if a password is already BCrypt encoded
   *
   * @param password the password to check
   * @return true if the password is BCrypt encoded, false otherwise
   */
  public boolean isPasswordEncoded(String password) {
    if (password == null || password.trim().isEmpty()) {
      return false;
    }

    // BCrypt hashes start with $2a$, $2b$, or $2y$ and have a specific length
    return (password.startsWith("$2a$") ||
        password.startsWith("$2b$") ||
        password.startsWith("$2y$")) &&
        password.length() >= 59; // BCrypt hashes are typically 60 characters
  }

  /**
   * Migrates a single user's password from plain text to BCrypt
   *
   * @param user the user whose password should be migrated
   */
  public void migrateUserPassword(Usuario user) {
    if (user == null || user.getSenha() == null) {
      logger.warn("Cannot migrate password for user: {} - user or password is null",
          user != null ? user.getEmail() : "unknown");
      return;
    }

    if (isPasswordEncoded(user.getSenha())) {
      logger.debug("Password for user {} is already encoded, skipping", user.getEmail());
      return;
    }

    try {
      String plainPassword = user.getSenha();
      String encodedPassword = passwordEncoder.encode(plainPassword);

      user.setSenha(encodedPassword);
      usuarioRepository.save(user);

      logger.info("Successfully migrated password for user: {}", user.getEmail());

    } catch (Exception e) {
      logger.error("Failed to migrate password for user: {} - Error: {}",
          user.getEmail(), e.getMessage(), e);
      throw new RuntimeException("Failed to migrate password for user: " + user.getEmail(), e);
    }
  }

  /**
   * Gets count of users with plain text passwords
   *
   * @return number of users with plain text passwords
   */
  public long getPlainTextPasswordCount() {
    try {
      List<Usuario> allUsers = usuarioRepository.findAll();
      return allUsers.stream()
          .filter(user -> user.getSenha() != null && !isPasswordEncoded(user.getSenha()))
          .count();
    } catch (Exception e) {
      logger.error("Error counting users with plain text passwords: {}", e.getMessage(), e);
      return -1;
    }
  }

  /**
   * Gets list of users with plain text passwords (for debugging)
   *
   * @return list of user emails with plain text passwords
   */
  public List<String> getUsersWithPlainTextPasswords() {
    try {
      List<Usuario> allUsers = usuarioRepository.findAll();
      return allUsers.stream()
          .filter(user -> user.getSenha() != null && !isPasswordEncoded(user.getSenha()))
          .map(Usuario::getEmail)
          .toList();
    } catch (Exception e) {
      logger.error("Error getting users with plain text passwords: {}", e.getMessage(), e);
      return List.of();
    }
  }
}
