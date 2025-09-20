package com.controle.fechamentocaixa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Controller para testes e debug
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @GetMapping("/usuarios")
  public List<Usuario> listarUsuarios() {
    return usuarioRepository.findAll();
  }

  @GetMapping("/health")
  public String health() {
    return "OK - Application is running";
  }

  @GetMapping("/public")
  public List<Usuario> listarUsuariosPublic() {
    return usuarioRepository.findAll();
  }

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private com.controle.fechamentocaixa.service.PasswordMigrationService passwordMigrationService;

  @GetMapping("/fix-passwords")
  public String fixPasswords() {
    try {
      long plainTextCount = passwordMigrationService.getPlainTextPasswordCount();
      if (plainTextCount == 0) {
        return "No users with plain text passwords found";
      }

      passwordMigrationService.migrateAllPasswords();
      return "Password migration completed. Check logs for details.";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  @GetMapping("/password-status")
  public String getPasswordStatus() {
    try {
      long plainTextCount = passwordMigrationService.getPlainTextPasswordCount();
      List<String> usersWithPlainText = passwordMigrationService.getUsersWithPlainTextPasswords();

      return String.format("Users with plain text passwords: %d. Users: %s",
          plainTextCount, usersWithPlainText);
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }
}
