package com.controle.fechamentocaixa.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controle.fechamentocaixa.dto.RegistroUsuarioRequest;
import com.controle.fechamentocaixa.dto.UsuarioResponse;
import com.controle.fechamentocaixa.exception.ResourceAlreadyExistsException;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Serviço para operações relacionadas a usuários
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra um novo usuário no sistema
     *
     * @param registroRequest Dados para registro do usuário
     * @return DTO com dados do usuário registrado
     * @throws ResourceAlreadyExistsException Se já existir um usuário com o email informado
     */
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest registroRequest) {
        // Verifica se já existe usuário com este email
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Usuário com email " + registroRequest.getEmail() + " já existe");
        }

        // Cria o novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registroRequest.getNome());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registroRequest.getSenha()));
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setAtivo(true);

        // Define os perfis do usuário
        Set<Perfil> perfis = new HashSet<>();
        if (registroRequest.getPerfis() != null && !registroRequest.getPerfis().isEmpty()) {
            registroRequest.getPerfis().forEach(perfilStr -> {
                try {
                    Perfil perfil = Perfil.valueOf(perfilStr);
                    perfis.add(perfil);
                } catch (IllegalArgumentException e) {
                    // Ignora perfil inválido
                }
            });
        }

        // Se nenhum perfil válido foi informado, adiciona CAIXA como padrão
        if (perfis.isEmpty()) {
            perfis.add(Perfil.CAIXA);
        }

        usuario.setPerfis(perfis);

        // Salva o usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Retorna DTO de resposta
        return converterParaUsuarioResponse(usuarioSalvo);
    }

    /**
     * Busca um usuário pelo ID
     *
     * @param id ID do usuário
     * @return DTO com dados do usuário
     * @throws ResourceNotFoundException Se o usuário não for encontrado
     */
    public UsuarioResponse buscarUsuarioPorId(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        return converterParaUsuarioResponse(usuario);
    }

    /**
     * Busca um usuário pelo email
     *
     * @param email Email do usuário
     * @return Optional contendo o usuário, se encontrado
     */
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Lista todos os usuários
     *
     * @return Lista de DTOs com dados dos usuários
     */
    public List<UsuarioResponse> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(this::converterParaUsuarioResponse)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza o status de ativação de um usuário
     *
     * @param id ID do usuário
     * @param ativo Novo status de ativação
     * @return DTO com dados do usuário atualizado
     * @throws ResourceNotFoundException Se o usuário não for encontrado
     */
    public UsuarioResponse atualizarStatusAtivacao(String id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setAtivo(ativo);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return converterParaUsuarioResponse(usuarioAtualizado);
    }

    /**
     * Registra o último acesso do usuário
     *
     * @param email Email do usuário que acessou o sistema
     */
    public void registrarAcesso(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            usuario.setDataUltimoAcesso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    /**
     * Converte um objeto Usuario para UsuarioResponse
     *
     * @param usuario Objeto Usuario a ser convertido
     * @return UsuarioResponse correspondente
     */
    private UsuarioResponse converterParaUsuarioResponse(Usuario usuario) {
        List<String> perfis = usuario.getPerfis()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfis(perfis)
                .ativo(usuario.isAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }
}
