package com.controle.fechamentocaixa.service;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controle.fechamentocaixa.model.Perfil;
import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Serviço para migração de dados e inicialização do sistema
 */
@Service
@Profile("migration")
public class DataMigrationService implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(DataMigrationService.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    logger.info("Iniciando migração de dados...");

    // Migra usuários existentes com perfis em formato string
    migrateExistingUsers();

    // Cria usuário admin padrão se não existir nenhum usuário
    createDefaultAdminUser();

    logger.info("Migração de dados concluída.");
  }

  /**
   * Migra usuários existentes que têm perfis em formato string para enum
   */
  private void migrateExistingUsers() {
    try {
      // Busca usuários que têm perfis como strings
      Query query = new Query(Criteria.where("perfis").type(2)); // Type 2 = String
      List<Usuario> usersToMigrate = mongoTemplate.find(query, Usuario.class);

      logger.info("Encontrados {} usuários para migração", usersToMigrate.size());

      for (Usuario user : usersToMigrate) {
        logger.info("Migrando usuário: {}", user.getEmail());

        // Remove o usuário antigo
        mongoTemplate.remove(Query.query(Criteria.where("id").is(user.getId())), Usuario.class);

        // Cria novo usuário com perfis corretos
        Set<Perfil> perfis = new HashSet<>();
        perfis.add(Perfil.ADMIN); // Assume ADMIN para usuários existentes

        // Encode the password if it's not already encoded
        String encodedPassword = user.getSenha();
        if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$")) {
          encodedPassword = passwordEncoder.encode(user.getSenha());
        }

        Usuario newUser = new Usuario();
        newUser.setId(user.getId());
        newUser.setEmail(user.getEmail());
        newUser.setNome(user.getNome());
        newUser.setSenha(encodedPassword);
        newUser.setPerfis(perfis);
        newUser.setAtivo(user.isAtivo());
        newUser.setDataCriacao(user.getDataCriacao() != null ? user.getDataCriacao() : LocalDateTime.now());
        newUser.setDataUltimoAcesso(user.getDataUltimoAcesso());

        mongoTemplate.save(newUser);
        logger.info("Usuário {} migrado com sucesso", user.getEmail());
      }

    } catch (Exception e) {
      logger.error("Erro durante migração de usuários: ", e);
    }
  }

  /**
   * Cria usuário admin padrão se não existir nenhum usuário no sistema
   */
  private void createDefaultAdminUser() {
    try {
      long userCount = usuarioRepository.count();

      if (userCount == 0) {
        logger.info("Nenhum usuário encontrado. Criando usuário admin padrão...");

        Set<Perfil> perfis = new HashSet<>();
        perfis.add(Perfil.ADMIN);

        Usuario adminUser = new Usuario();
        adminUser.setEmail("admin@fechamentocaixa.com");
        adminUser.setNome("Administrador");
        adminUser.setSenha(passwordEncoder.encode("admin123"));
        adminUser.setPerfis(perfis);
        adminUser.setAtivo(true);
        adminUser.setDataCriacao(LocalDateTime.now());

        usuarioRepository.save(adminUser);
        logger.info("Usuário admin padrão criado: admin@fechamentocaixa.com / admin123");
      }

    } catch (Exception e) {
      logger.error("Erro ao criar usuário admin padrão: ", e);
    }
  }
}
