package com.controle.fechamentocaixa.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para testar endpoints públicos e protegidos
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * Endpoint público para teste
     *
     * @return Mensagem de saudação
     */
    @GetMapping("/public")
    public String publicAccess() {
        return "Conteúdo público";
    }

    /**
     * Endpoint protegido para qualquer usuário autenticado
     *
     * @return Mensagem de saudação para usuário autenticado
     */
    @GetMapping("/user")
    public String userAccess() {
        return "Conteúdo para usuário autenticado";
    }

    /**
     * Endpoint protegido para administradores
     *
     * @return Mensagem de saudação para administrador
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Conteúdo para administrador";
    }

    /**
     * Endpoint protegido para gerentes
     *
     * @return Mensagem de saudação para gerente
     */
    @GetMapping("/gerente")
    @PreAuthorize("hasRole('GERENTE')")
    public String gerenteAccess() {
        return "Conteúdo para gerente";
    }

    /**
     * Endpoint protegido para caixas
     *
     * @return Mensagem de saudação para caixa
     */
    @GetMapping("/caixa")
    @PreAuthorize("hasRole('CAIXA')")
    public String caixaAccess() {
        return "Conteúdo para caixa";
    }
}
