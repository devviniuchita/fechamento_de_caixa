package com.controle.fechamentocaixa.model;

/**
 * Enumeração de perfis de usuário no sistema
 */
public enum Perfil {
    ADMIN("ROLE_ADMIN"),
    GERENTE("ROLE_GERENTE"),
    CAIXA("ROLE_CAIXA");

    private String role;

    Perfil(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
