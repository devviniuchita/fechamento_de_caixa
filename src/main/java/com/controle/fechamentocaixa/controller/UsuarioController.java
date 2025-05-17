package com.controle.fechamentocaixa.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controle.fechamentocaixa.dto.UsuarioResponse;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;
import com.controle.fechamentocaixa.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

/**
 * Controller para operações com usuários
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Lista todos os usuários
     * 
     * @return Lista de usuários
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Dados do usuário
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or #id == authentication.principal.id")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        
        return ResponseEntity.ok(mapToUsuarioResponse(usuario));
    }
    
    /**
     * Ativa um usuário
     * 
     * @param id ID do usuário
     * @return Mensagem de sucesso
     */
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ativarUsuario(@PathVariable String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Usuário ativado com sucesso!");
    }
    
    /**
     * Desativa um usuário
     * 
     * @param id ID do usuário
     * @return Mensagem de sucesso
     */
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desativarUsuario(@PathVariable String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        
        // Verifica se é o último admin
        if (usuario.isAdmin() && contarAdminsAtivos() <= 1) {
            return ResponseEntity.badRequest().body("Não é possível desativar o último administrador!");
        }
        
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Usuário desativado com sucesso!");
    }
    
    /**
     * Altera a senha do usuário
     * 
     * @param id ID do usuário
     * @param request Requisição com nova senha
     * @return Mensagem de sucesso
     */
    @PatchMapping("/{id}/senha")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> alterarSenha(@PathVariable String id, @Valid @RequestBody AlteraSenhaRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        
        // Se não for admin, verifica senha atual
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) && 
                !id.equals(userDetails.getId())) {
            return ResponseEntity.badRequest().body("Você não tem permissão para alterar a senha deste usuário!");
        }
        
        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
    
    /**
     * Converte entidade Usuario para DTO UsuarioResponse
     * 
     * @param usuario Entidade Usuario
     * @return DTO UsuarioResponse
     */
    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
        List<String> perfisStr = usuario.getPerfis().stream()
                .map(perfil -> perfil.getRole())
                .collect(Collectors.toList());
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfis(perfisStr)
                .ativo(usuario.isAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }
    
    /**
     * Conta o número de administradores ativos
     * 
     * @return Número de administradores ativos
     */
    private long contarAdminsAtivos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.isAtivo() && u.isAdmin())
                .count();
    }
    
    /**
     * DTO para alteração de senha
     */
    public static class AlteraSenhaRequest {
        private String novaSenha;
        
        public String getNovaSenha() {
            return novaSenha;
        }
        
        public void setNovaSenha(String novaSenha) {
            this.novaSenha = novaSenha;
        }
    }
} 