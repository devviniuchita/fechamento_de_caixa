package com.controle.fechamentocaixa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.controle.fechamentocaixa.model.Usuario;
import com.controle.fechamentocaixa.repository.UsuarioRepository;

/**
 * Controller para testes e debug
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private com.controle.fechamentocaixa.security.services.UserDetailsServiceImpl userDetailsService;

  @Autowired
  private com.controle.fechamentocaixa.security.jwt.JwtUtils jwtUtils;

  @GetMapping("/usuarios")
  public List<Usuario> listarUsuarios() {
    return usuarioRepository.findAll();
  }

  @GetMapping("/health")
  public String health() {
    return "OK - Application is running";
  }

  /**
   * Emite um JWT para o email informado para diagnóstico de problemas no JwtUtils
   */
  @GetMapping("/issue-jwt")
  public Object issueJwt(@RequestParam String email) {
    try {
      var ud = userDetailsService.loadUserByUsername(email);
      var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
          ud, null, ud.getAuthorities());
      String token = jwtUtils.generateJwtToken(auth);
      return java.util.Map.of("token", token, "authorities", ud.getAuthorities().toString());
    } catch (Exception e) {
      return java.util.Map.of("status", "ERROR", "message", e.getMessage());
    }
  }

  @GetMapping("/public")
  public List<Usuario> listarUsuariosPublic() {
    return usuarioRepository.findAll();
  }

  @GetMapping("/emails")
  public java.util.List<String> listarEmails() {
    return usuarioRepository.findAll().stream().map(Usuario::getEmail).toList();
  }

  @GetMapping("/find")
  public Object findByEmail(@RequestParam String email) {
    var user = mongoTemplate.findOne(new org.springframework.data.mongodb.core.query.Query(
        org.springframework.data.mongodb.core.query.Criteria.where("email").is(email)),
        com.controle.fechamentocaixa.model.Usuario.class);
    return user != null ? user : java.util.Map.of("status", "NOT_FOUND", "email", email);
  }

  /**
   * Cria um usuário arbitrário (apenas DEV)
   */
  @PostMapping("/create-user")
  public Object createUser(@RequestParam String email,
      @RequestParam String senha,
      @RequestParam(defaultValue = "USER") String perfil,
      @RequestParam(defaultValue = "Novo Usuário") String nome) {
    if (usuarioRepository.existsByEmail(email)) {
      return java.util.Map.of("status", "ALREADY_EXISTS", "email", email);
    }
    com.controle.fechamentocaixa.model.Usuario u = new com.controle.fechamentocaixa.model.Usuario();
    u.setEmail(email);
    u.setNome(nome);
    u.setSenha(passwordEncoder.encode(senha));
    java.util.Set<com.controle.fechamentocaixa.model.Perfil> perfis = new java.util.HashSet<>();
    try {
      var p = com.controle.fechamentocaixa.model.Perfil.valueOf(perfil.toUpperCase());
      perfis.add(p);
    } catch (IllegalArgumentException e) {
      // default
      perfis.add(com.controle.fechamentocaixa.model.Perfil.CAIXA);
    }
    u.setPerfis(perfis);
    u.setAtivo(true);
    usuarioRepository.save(u);
    return java.util.Map.of("status", "CREATED", "email", email, "perfil", perfis.toString());
  }

  /**
   * Diagnóstico: tenta autenticar via AuthenticationManager e retorna o resultado
   */
  @PostMapping("/debug-login")
  public Object debugLogin(@RequestParam String email, @RequestParam String senha) {
    try {
      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, senha));
      var principal = auth.getPrincipal();
      java.util.Collection<?> authorities = auth.getAuthorities();
      return java.util.Map.of(
          "status", "OK",
          "principalClass", principal != null ? principal.getClass().getName() : null,
          "authorities", authorities.toString());
    } catch (Exception e) {
      return java.util.Map.of(
          "status", "FAIL",
          "message", e.getMessage());
    }
  }

  @GetMapping("/debug-login")
  public Object debugLoginGet(@RequestParam String email, @RequestParam String senha) {
    return debugLogin(email, senha);
  }

  @GetMapping("/check-user")
  public Object checkUser(@RequestParam String email, @RequestParam String senha) {
    var user = mongoTemplate.findOne(new org.springframework.data.mongodb.core.query.Query(
        org.springframework.data.mongodb.core.query.Criteria.where("email").is(email)),
        com.controle.fechamentocaixa.model.Usuario.class);
    if (user == null) {
      return java.util.Map.of("status", "NOT_FOUND", "email", email);
    }
    boolean matches = passwordEncoder.matches(senha, user.getSenha());
    return java.util.Map.of(
        "status", "FOUND",
        "email", user.getEmail(),
        "ativo", user.isAtivo(),
        "perfis", user.getPerfis(),
        "matches", matches);
  }

  @Autowired
  private com.controle.fechamentocaixa.service.PasswordMigrationService passwordMigrationService;

  @GetMapping("/fix-passwords")
  public String fixPasswords() {
    try {
      long plainTextCount = passwordMigrationService.getPlainTextPasswordCount();
      if (plainTextCount == 0) {
        return "No users with plain text passwords found";
      }

      passwordMigrationService.migrateAllPasswords();
      return "Password migration completed. Check logs for details.";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  @GetMapping("/password-status")
  public String getPasswordStatus() {
    try {
      long plainTextCount = passwordMigrationService.getPlainTextPasswordCount();
      List<String> usersWithPlainText = passwordMigrationService.getUsersWithPlainTextPasswords();

      return String.format("Users with plain text passwords: %d. Users: %s",
          plainTextCount, usersWithPlainText);
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  /**
   * Endpoint de utilidade para resetar/definir a senha de um usuário durante o
   * desenvolvimento
   * ATENÇÃO: este endpoint está aberto (permitAll) por estar sob /test e deve ser
   * utilizado apenas em ambiente de dev
   */
  @PostMapping("/reset-password")
  public String resetPassword(@RequestParam String email, @RequestParam String senha) {
    try {
      return usuarioRepository.findByEmail(email).map(user -> {
        user.setSenha(passwordEncoder.encode(senha));
        usuarioRepository.save(user);
        return "Password reset for user: " + email;
      }).orElse("User not found: " + email);
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  /**
   * Força a criação/atualização do usuário admin de teste (admin@test.com /
   * admin123)
   * Útil quando há inconsistência de dados entre serviços de migração/seed
   */
  @PostMapping("/ensure-admin-test")
  public String ensureAdminTest() {
    try {
      String testEmail = "admin@test.com";
      // Upsert explícito com MongoTemplate para evitar comportamento anômalo
      org.bson.Document query = new org.bson.Document("email", testEmail);
      org.bson.Document update = new org.bson.Document("$set",
          new org.bson.Document("email", testEmail)
              .append("nome", "Test Admin")
              .append("senha", passwordEncoder.encode("admin123"))
              .append("perfis", java.util.List.of("ADMIN"))
              .append("ativo", true)
              .append("_class", "com.controle.fechamentocaixa.model.Usuario"));
      var result = mongoTemplate.getCollection("usuarios").updateOne(query, update,
          new com.mongodb.client.model.UpdateOptions().upsert(true));
      if (result.getUpsertedId() != null) {
        return "Admin test user created: " + testEmail + " / admin123";
      }
      return "Admin test user updated: " + testEmail + " / admin123";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  /**
   * DEV ONLY: Busca bruta na coleção por email exato
   */
  @GetMapping("/raw-find")
  public Object rawFind(@RequestParam String email) {
    var doc = mongoTemplate.getCollection("usuarios").find(new org.bson.Document("email", email)).first();
    return doc != null ? doc : java.util.Map.of("status", "NOT_FOUND", "email", email);
  }

  @GetMapping("/load-user-details")
  public Object loadUserDetails(@RequestParam String email) {
    try {
      var ud = userDetailsService.loadUserByUsername(email);
      return java.util.Map.of(
          "status", "OK",
          "username", ud.getUsername(),
          "enabled", ud.isEnabled(),
          "authorities", ud.getAuthorities().toString(),
          "passwordPrefix",
          ud.getPassword() != null && ud.getPassword().length() >= 4 ? ud.getPassword().substring(0, 4) : null);
    } catch (Exception e) {
      return java.util.Map.of("status", "FAIL", "message", e.getMessage());
    }
  }

  @GetMapping("/db-info")
  public Object dbInfo() {
    try {
      String dbName = mongoTemplate.getDb().getName();
      long repoCount = usuarioRepository.count();
      long templateCount = mongoTemplate.getCollection("usuarios").countDocuments();
      var firstDoc = mongoTemplate.getCollection("usuarios").find().first();
      return java.util.Map.of(
          "db", dbName,
          "repoCount", repoCount,
          "templateCount", templateCount,
          "firstDoc", firstDoc);
    } catch (Exception e) {
      return java.util.Map.of("status", "ERROR", "message", e.getMessage());
    }
  }

  /**
   * DEV ONLY: verifica match de senha usando o hash bruto do documento
   */
  @GetMapping("/matches-raw")
  public Object matchesRaw(@RequestParam String email, @RequestParam String senha) {
    var doc = mongoTemplate.getCollection("usuarios").find(new org.bson.Document("email", email)).first();
    if (doc == null) {
      return java.util.Map.of("status", "NOT_FOUND", "email", email);
    }
    String hash = doc.getString("senha");
    boolean matches = false;
    if (hash != null) {
      matches = passwordEncoder.matches(senha, hash);
    }
    return java.util.Map.of("status", "FOUND", "email", doc.getString("email"), "matches", matches, "hashPrefix",
        hash != null && hash.length() >= 4 ? hash.substring(0, 4) : null);
  }

  /**
   * Verifica se a senha fornecida confere com o hash do usuário
   */
  @PostMapping("/check-auth")
  public String checkAuth(@RequestParam String email, @RequestParam String senha) {
    var userOpt = usuarioRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      return "User not found: " + email;
    }
    var user = userOpt.get();
    boolean matches = passwordEncoder.matches(senha, user.getSenha());
    return "matches=" + matches + ", ativo=" + user.isAtivo() + ", perfis=" + user.getPerfis();
  }

  /**
   * DEV ONLY: Remove todos os usuários da coleção
   */
  @PostMapping("/wipe-users")
  public Object wipeUsers() {
    long before = usuarioRepository.count();
    try {
      // Drop da coleção para limpeza total
      mongoTemplate.getCollection("usuarios").drop();
      // Recria a coleção (será recriada automaticamente na próxima inserção)
    } catch (Exception e) {
      return java.util.Map.of("status", "ERROR", "message", e.getMessage());
    }
    long after = 0L;
    return java.util.Map.of("status", "WIPED", "before", before, "after", after);
  }

  /**
   * DEV ONLY: Retorna a contagem de documentos em usuarios
   */
  @GetMapping("/count-users")
  public Object countUsers() {
    return java.util.Map.of("count", usuarioRepository.count());
  }

  /**
   * DEV ONLY: Lista documentos brutos da coleção usuarios (limitado)
   */
  @GetMapping("/raw-users")
  public Object rawUsers(@RequestParam(name = "limit", defaultValue = "10") int limit) {
    var docs = mongoTemplate.getCollection("usuarios").find().limit(limit);
    java.util.List<org.bson.Document> list = new java.util.ArrayList<>();
    for (org.bson.Document d : docs) {
      list.add(d);
    }
    return list;
  }

  /**
   * DEV ONLY: insere um documento bruto na coleção usuarios
   */
  @PostMapping("/insert-raw")
  public Object insertRaw(@RequestParam String email, @RequestParam String nome, @RequestParam String senha) {
    org.bson.Document d = new org.bson.Document()
        .append("email", email)
        .append("nome", nome)
        .append("senha", senha)
        .append("perfis", java.util.List.of("ADMIN"))
        .append("ativo", true)
        .append("_class", "com.controle.fechamentocaixa.model.Usuario");
    mongoTemplate.getCollection("usuarios").insertOne(d);
    return java.util.Map.of("status", "INSERTED", "email", email);
  }

  /**
   * DEV ONLY: atualiza o campo email de um documento pelo email atual
   */
  @PostMapping("/update-raw-email")
  public Object updateRawEmail(@RequestParam String currentEmail, @RequestParam String newEmail) {
    var coll = mongoTemplate.getCollection("usuarios");
    var result = coll.updateOne(new org.bson.Document("email", currentEmail),
        new org.bson.Document("$set", new org.bson.Document("email", newEmail)));
    return java.util.Map.of("matched", result.getMatchedCount(), "modified", result.getModifiedCount());
  }

  /**
   * DEV ONLY: Reset completo do ambiente de usuários em uma chamada:
   * 1) wipe (drop da coleção)
   * 2) ensure-admin (upsert admin@test.com / admin123)
   * 3) sanity checks (contagens, documento bruto, match de senha, user details,
   * debug-login)
   */
  @PostMapping("/reset-dev")
  public Object resetDev() {
    java.util.Map<String, Object> report = new java.util.LinkedHashMap<>();
    String testEmail = "admin@test.com";
    String testSenha = "admin123";
    try {
      // 1) Wipe
      long before = usuarioRepository.count();
      mongoTemplate.getCollection("usuarios").drop();
      long after = 0L;
      report.put("wipe", java.util.Map.of("before", before, "after", after, "status", "WIPED"));

      // 2) Ensure admin via upsert bruto
      org.bson.Document query = new org.bson.Document("email", testEmail);
      org.bson.Document update = new org.bson.Document("$set",
          new org.bson.Document("email", testEmail)
              .append("nome", "Test Admin")
              .append("senha", passwordEncoder.encode(testSenha))
              .append("perfis", java.util.List.of("ADMIN"))
              .append("ativo", true)
              .append("_class", "com.controle.fechamentocaixa.model.Usuario"));
      var upsertRes = mongoTemplate.getCollection("usuarios").updateOne(query, update,
          new com.mongodb.client.model.UpdateOptions().upsert(true));
      boolean created = upsertRes.getUpsertedId() != null;
      report.put("ensureAdmin", java.util.Map.of(
          "result", created ? "CREATED" : "UPDATED",
          "upsertedId", created ? upsertRes.getUpsertedId().toString() : null));

      // 3) Sanity checks
      long repoCount = usuarioRepository.count();
      long templateCount = mongoTemplate.getCollection("usuarios").countDocuments();
      var rawDoc = mongoTemplate.getCollection("usuarios").find(new org.bson.Document("email", testEmail)).first();
      String rawEmail = rawDoc != null ? rawDoc.getString("email") : null;
      String rawNome = rawDoc != null ? rawDoc.getString("nome") : null;
      String rawHash = rawDoc != null ? rawDoc.getString("senha") : null;
      boolean rawMatches = rawHash != null && passwordEncoder.matches(testSenha, rawHash);

      java.util.Map<String, Object> userDetailsInfo;
      try {
        var ud = userDetailsService.loadUserByUsername(testEmail);
        userDetailsInfo = java.util.Map.of(
            "loaded", true,
            "username", ud.getUsername(),
            "enabled", ud.isEnabled(),
            "authorities", ud.getAuthorities().toString(),
            "passwordPrefix",
            ud.getPassword() != null && ud.getPassword().length() >= 4 ? ud.getPassword().substring(0, 4) : null);
      } catch (Exception e) {
        userDetailsInfo = java.util.Map.of("loaded", false, "message", e.getMessage());
      }

      java.util.Map<String, Object> checkUserInfo;
      {
        var user = mongoTemplate.findOne(new org.springframework.data.mongodb.core.query.Query(
            org.springframework.data.mongodb.core.query.Criteria.where("email").is(testEmail)),
            com.controle.fechamentocaixa.model.Usuario.class);
        if (user == null) {
          checkUserInfo = java.util.Map.of("status", "NOT_FOUND");
        } else {
          boolean matches = passwordEncoder.matches(testSenha, user.getSenha());
          checkUserInfo = java.util.Map.of(
              "status", "FOUND",
              "email", user.getEmail(),
              "ativo", user.isAtivo(),
              "perfis", user.getPerfis(),
              "matches", matches);
        }
      }

      java.util.Map<String, Object> debugLoginInfo;
      try {
        var auth = authenticationManager
            .authenticate(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testEmail,
                testSenha));
        var principal = auth.getPrincipal();
        debugLoginInfo = java.util.Map.of(
            "status", "OK",
            "principalClass", principal != null ? principal.getClass().getName() : null,
            "authorities", auth.getAuthorities().toString());
      } catch (Exception e) {
        debugLoginInfo = java.util.Map.of("status", "FAIL", "message", e.getMessage());
      }

      report.put("sanity", java.util.Map.of(
          "db", mongoTemplate.getDb().getName(),
          "repoCount", repoCount,
          "templateCount", templateCount,
          "raw", java.util.Map.of(
              "email", rawEmail,
              "nome", rawNome,
              "hashPrefix", rawHash != null && rawHash.length() >= 4 ? rawHash.substring(0, 4) : null,
              "matchesRaw", rawMatches),
          "userDetails", userDetailsInfo,
          "checkUser", checkUserInfo,
          "debugLogin", debugLoginInfo));

      report.put("status", "DONE");
      return report;
    } catch (Exception e) {
      return java.util.Map.of("status", "ERROR", "message", e.getMessage());
    }
  }
}
