package com.controle.fechamentocaixa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controle.fechamentocaixa.dto.LoginRequest;
import com.controle.fechamentocaixa.dto.LoginResponse;
import com.controle.fechamentocaixa.dto.RegistroUsuarioRequest;
import com.controle.fechamentocaixa.dto.UsuarioResponse;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para autenticar um usuário
     *
     * @param loginRequest DTO com credenciais de login
     * @return ResponseEntity com token JWT e informações do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        // Registra o acesso do usuário
        usuarioService.registrarAcesso(userDetails.getEmail());

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
            if (usuarioRepository.count() == 0 && (registroRequest.getPerfis() == null || !registroRequest.getPerfis().contains("ADMIN"))) {
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
