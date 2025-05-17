package com.controle.fechamentocaixa.security.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.controle.fechamentocaixa.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementação do UserDetails do Spring Security
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;
    private String email;
    private String nome;
    
    @JsonIgnore
    private String password;
    
    private boolean ativo;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String email, String nome, String password, boolean ativo,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.password = password;
        this.ativo = ativo;
        this.authorities = authorities;
    }

    /**
     * Cria uma instância de UserDetailsImpl a partir de um Usuario
     * 
     * @param user Entidade Usuario
     * @return UserDetailsImpl
     */
    public static UserDetailsImpl build(Usuario user) {
        List<GrantedAuthority> authorities = user.getPerfis().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getNome(),
                user.getSenha(),
                user.isAtivo(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    
    public String getNome() {
        return nome;
    }
    
    public boolean isAtivo() {
        return ativo;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return id.equals(user.id);
    }
}