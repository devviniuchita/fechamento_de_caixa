package com.controle.fechamentocaixa.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controle.fechamentocaixa.dto.LoginRequest;
import com.controle.fechamentocaixa.dto.LoginResponse;
import com.controle.fechamentocaixa.dto.RegistroUsuarioRequest;
import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;
import com.controle.fechamentocaixa.security.jwt.JwtUtils;
import com.controle.fechamentocaixa.security.services.UserDetailsImpl;

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
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

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
                .collect(Collectors.toList());
        
        // Atualiza último acesso
        usuarioRepository.findById(userDetails.getId()).ifPresent(usuario -> {
            usuario.setDataUltimoAcesso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });

        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .nome(userDetails.getNome())
                .perfis(roles)
                .build());
    }

    /**
     * Endpoint para registrar um novo usuário
     * 
     * @param registroRequest DTO com dados do novo usuário
     * @return ResponseEntity com mensagem de sucesso
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroUsuarioRequest registroRequest) {
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Erro: Email já está em uso!");
        }

        // Cria nova conta de usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registroRequest.getNome());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setSenha(encoder.encode(registroRequest.getSenha()));
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setAtivo(true);

        Set<String> strRoles = registroRequest.getPerfis();
        Set<Perfil> roles = new HashSet<>();

        // Se nenhum perfil for especificado, atribui CAIXA por padrão
        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(Perfil.CAIXA);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                case "admin":
                    roles.add(Perfil.ADMIN);
                    break;
                case "gerente":
                    roles.add(Perfil.GERENTE);
                    break;
                default:
                    roles.add(Perfil.CAIXA);
                }
            });
        }

        // Se for o primeiro usuário, atribui ADMIN
        if (usuarioRepository.count() == 0) {
            roles.add(Perfil.ADMIN);
        }

        usuario.setPerfis(roles);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
} 