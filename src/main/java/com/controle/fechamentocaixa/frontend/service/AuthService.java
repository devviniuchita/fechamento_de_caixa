package com.controle.fechamentocaixa.frontend.service;

import com.controle.fechamentocaixa.model.User;

/**
 * Interface para serviço de autenticação.
 * Fornece métodos para autenticação e gerenciamento de sessão do usuário.
 */
public interface AuthService {
    
    /**
     * Realiza o login do usuário.
     *
     * @param username nome de usuário
     * @param password senha
     * @return usuário autenticado
     * @throws RuntimeException se a autenticação falhar
     */
    User login(String username, String password);
    
    /**
     * Realiza o logout do usuário atual.
     */
    void logout();
    
    /**
     * Retorna o usuário atualmente autenticado.
     *
     * @return usuário autenticado ou null se não houver usuário autenticado
     */
    User getCurrentUser();
    
    /**
     * Verifica se há um usuário autenticado.
     *
     * @return true se houver um usuário autenticado, false caso contrário
     */
    boolean isAuthenticated();
} 