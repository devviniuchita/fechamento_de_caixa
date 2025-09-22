package com.controle.fechamentocaixa.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do serviço UserDetailsService para autenticação com Spring
 * Security
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * Carrega os detalhes do usuário pelo email (username)
   *
   * @param email Email do usuário
   * @return UserDetails para autenticação
   * @throws UsernameNotFoundException se o usuário não for encontrado
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Busca documento diretamente para evitar qualquer conversão anômala
    org.bson.Document doc = mongoTemplate.getCollection("usuarios").find(new org.bson.Document("email", email)).first();
    if (doc == null) {
      throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }

    String emailStr = doc.getString("email");
    String nomeStr = doc.getString("nome");
    String senhaHash = doc.getString("senha");
    Boolean ativo = doc.getBoolean("ativo", Boolean.TRUE);

    // Extração type-safe da lista de perfis
    Object perfisObj = doc.get("perfis");
    java.util.List<String> perfisStr = new java.util.ArrayList<>();
    if (perfisObj instanceof java.util.List<?>) {
      for (Object pObj : (java.util.List<?>) perfisObj) {
        if (pObj != null) {
          perfisStr.add(String.valueOf(pObj));
        }
      }
    } else if (perfisObj instanceof String) {
      perfisStr.add((String) perfisObj);
    }
    java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
    if (perfisStr.isEmpty()) {
      authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
          com.controle.fechamentocaixa.model.Perfil.CAIXA.getRole()));
    } else {
      for (String p : perfisStr) {
        try {
          com.controle.fechamentocaixa.model.Perfil perfil = com.controle.fechamentocaixa.model.Perfil.valueOf(p);
          authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(perfil.getRole()));
        } catch (IllegalArgumentException ex) {
          // fallback: trata como CAIXA
          authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
              com.controle.fechamentocaixa.model.Perfil.CAIXA.getRole()));
        }
      }
    }

    return new UserDetailsImpl(null, emailStr, nomeStr, senhaHash, ativo, authorities);
  }
}
