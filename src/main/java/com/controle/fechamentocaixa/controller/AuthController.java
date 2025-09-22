package com.controle.fechamentocaixa.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.controle.fechamentocaixa.config.AppProperties;
import com.controle.fechamentocaixa.dto.*;
import com.controle.fechamentocaixa.repository.UsuarioRepository;
import com.controle.fechamentocaixa.security.jwt.JwtUtils;
import com.controle.fechamentocaixa.security.services.UserDetailsImpl;
import com.controle.fechamentocaixa.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Controller para autenticação de usuários
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private AppProperties appProperties;

  /**
   * Endpoint para autenticar um usuário
   *
   * @param loginRequest DTO com credenciais de login
   * @return ResponseEntity com token JWT e informações do usuário
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = null;
    try {
      authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));
    } catch (org.springframework.security.authentication.BadCredentialsException ex) {
      // Fallback de DEV: permite login virtual quando habilitado e sem depender do
      // banco
      if (appProperties.getDevAuth() != null && appProperties.getDevAuth().isEnabled()) {
        if (appProperties.getDevAuth().getEmail().equalsIgnoreCase(loginRequest.getEmail())
            && appProperties.getDevAuth().getPassword().equals(loginRequest.getSenha())) {
          // Cria principal virtual
          java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> auths = appProperties
              .getDevAuth().getRoles().stream()
              .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r))
              .toList();
          UserDetailsImpl virtual = new UserDetailsImpl("dev-user-id",
              loginRequest.getEmail(), "Dev User", "[virtual]", true, auths);
          authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
              virtual, null, auths);
        } else {
          throw ex;
        }
      } else {
        throw ex;
      }
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt;
    try {
      jwt = jwtUtils.generateJwtToken(authentication);
    } catch (Exception e) {
      log.error("Falha ao gerar JWT: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError().body(java.util.Map.of(
          "error", "Falha ao gerar token",
          "message", e.getMessage()));
    }

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .toList();

    // Registra o acesso do usuário
    try {
      usuarioService.registrarAcesso(userDetails.getEmail());
    } catch (Exception e) {
      // Não falha o login por causa de auditoria
      log.warn("Falha ao registrar acesso para {}: {}", userDetails.getEmail(), e.getMessage());
    }

    LoginResponse response = new LoginResponse();
    response.setToken(jwt);
    response.setId(userDetails.getId());
    response.setEmail(userDetails.getEmail());
    response.setNome(userDetails.getNome());
    response.setPerfis(roles);

    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para registrar um novo usuário
   *
   * @param registroRequest DTO com dados do novo usuário
   * @return ResponseEntity com mensagem de sucesso
   */
  @PostMapping("/registrar")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroUsuarioRequest registroRequest) {
    try {
      // Verifica se é o primeiro usuário e adiciona perfil ADMIN
      if (usuarioRepository.count() == 0
          && (registroRequest.getPerfis() == null || !registroRequest.getPerfis().contains("ADMIN"))) {
        if (registroRequest.getPerfis() == null) {
          registroRequest.setPerfis(java.util.Set.of("ADMIN"));
        } else {
          registroRequest.getPerfis().add("ADMIN");
        }
      }

      UsuarioResponse usuarioResponse = usuarioService.registrarUsuario(registroRequest);
      return ResponseEntity.ok(usuarioResponse);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
