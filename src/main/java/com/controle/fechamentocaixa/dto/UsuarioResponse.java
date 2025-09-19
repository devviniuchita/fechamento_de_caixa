package com.controle.fechamentocaixa.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta com dados de usuário
 */
public class UsuarioResponse {

    private String id;
    private String nome;
    private String email;
    private List<String> perfis;
    private boolean ativo;
    private LocalDateTime dataCriacao;

    public UsuarioResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<String> perfis) {
        this.perfis = perfis;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public static UsuarioResponseBuilder builder() {
        return new UsuarioResponseBuilder();
    }

    public static class UsuarioResponseBuilder {
        private String id;
        private String nome;
        private String email;
        private List<String> perfis;
        private boolean ativo;
        private LocalDateTime dataCriacao;

        public UsuarioResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UsuarioResponseBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public UsuarioResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UsuarioResponseBuilder perfis(List<String> perfis) {
            this.perfis = perfis;
            return this;
        }

        public UsuarioResponseBuilder ativo(boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public UsuarioResponseBuilder dataCriacao(LocalDateTime dataCriacao) {
            this.dataCriacao = dataCriacao;
            return this;
        }

        public UsuarioResponse build() {
            UsuarioResponse response = new UsuarioResponse();
            response.setId(id);
            response.setNome(nome);
            response.setEmail(email);
            response.setPerfis(perfis);
            response.setAtivo(ativo);
            response.setDataCriacao(dataCriacao);
            return response;
        }
    }
}
