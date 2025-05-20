# Especificação do Módulo de Usuários e Autenticação JWT

## Visão Geral

O módulo de usuários e autenticação JWT é responsável pelo controle de acesso ao Sistema de Fechamento de Caixa, garantindo que apenas usuários autorizados possam acessar as funcionalidades de acordo com seus perfis. Este documento detalha o modelo de dados, endpoints, fluxos de autenticação e requisitos de segurança para implementação.

## Modelo de Dados

### Usuário

```java
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String nome;
    
    private String senha; // Armazenada com BCrypt
    
    private Set<Perfil> perfis = new HashSet<>();
    
    private boolean ativo = true;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataUltimoAcesso;
    
    // Getters, setters, etc.
}
```

### Perfil (Enum)

```java
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
```

### DTO de Autenticação

```java
public class LoginRequest {
    @NotBlank
    private String email;
    
    @NotBlank
    private String senha;
    
    // Getters, setters
}

public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private String id;
    private String nome;
    private String email;
    private List<String> perfis;
    
    // Getters, setters
}

public class RegistroUsuarioRequest {
    @NotBlank
    private String nome;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String senha;
    
    private Set<String> perfis;
    
    // Getters, setters
}

public class UsuarioResponse {
    private String id;
    private String nome;
    private String email;
    private List<String> perfis;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    
    // Getters, setters
}
```

## Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| POST | /auth/login | Autenticar usuário e gerar token JWT | Público |
| POST | /auth/refresh | Renovar token JWT | Autenticado |

### Gestão de Usuários

| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| GET | /usuarios | Listar todos os usuários | ADMIN, GERENTE |
| GET | /usuarios/{id} | Obter usuário específico | ADMIN, GERENTE, Próprio usuário |
| POST | /usuarios | Criar novo usuário | ADMIN |
| PUT | /usuarios/{id} | Atualizar usuário existente | ADMIN, Próprio usuário (limitado) |
| PATCH | /usuarios/{id}/senha | Alterar senha | ADMIN, Próprio usuário |
| PATCH | /usuarios/{id}/ativar | Ativar usuário | ADMIN |
| PATCH | /usuarios/{id}/desativar | Desativar usuário | ADMIN |

## Fluxo de Autenticação

1. **Login**:
   - Cliente envia credenciais (email/senha) para `/auth/login`
   - Servidor valida credenciais contra banco de dados
   - Se válido, gera token JWT e retorna com informações do usuário
   - Se inválido, retorna erro 401 (Unauthorized)

2. **Acesso a Recursos Protegidos**:
   - Cliente inclui token JWT no header Authorization (`Bearer {token}`)
   - Servidor valida token (assinatura, expiração)
   - Servidor verifica permissões do usuário para o recurso solicitado
   - Se autorizado, processa a requisição
   - Se não autorizado, retorna erro 403 (Forbidden)

3. **Renovação de Token**:
   - Cliente envia token atual para `/auth/refresh`
   - Servidor valida token e gera novo token com nova data de expiração
   - Retorna novo token para o cliente

## Configuração de Segurança

### JWT Configuration

```java
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    
    // Métodos para gerar e validar tokens
}
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    
    // Configuração de segurança, autenticação e autorização
}
```

## Regras de Negócio

1. **Criação de Usuários**:
   - Apenas administradores podem criar novos usuários
   - Email deve ser único no sistema
   - Senha deve ter no mínimo 6 caracteres
   - Primeiro usuário criado recebe perfil ADMIN automaticamente

2. **Autenticação**:
   - Usuários inativos não podem fazer login
   - Senhas são armazenadas com hash BCrypt
   - Tokens JWT expiram após 24 horas (configurável)
   - Tentativas de login malsucedidas são registradas

3. **Autorização**:
   - ADMIN: acesso total ao sistema
   - GERENTE: acesso a relatórios e visualização de todos os fechamentos
   - CAIXA: acesso apenas aos próprios fechamentos e operações básicas

4. **Gestão de Perfis**:
   - Apenas ADMIN pode alterar perfis de usuários
   - Um usuário pode ter múltiplos perfis
   - Último ADMIN do sistema não pode ser desativado ou rebaixado

## Validações e Tratamento de Erros

1. **Validações de Entrada**:
   - Email válido (formato padrão)
   - Senha com requisitos mínimos
   - Campos obrigatórios preenchidos

2. **Tratamento de Erros**:
   - Erro 400: Bad Request (dados inválidos)
   - Erro 401: Unauthorized (credenciais inválidas)
   - Erro 403: Forbidden (sem permissão)
   - Erro 404: Not Found (usuário não encontrado)
   - Erro 409: Conflict (email já existe)
   - Erro 500: Internal Server Error (erro no servidor)

## Logs e Auditoria

1. **Logs de Autenticação**:
   - Registrar tentativas de login (sucesso/falha)
   - Registrar geração e renovação de tokens
   - Registrar logout (quando implementado)

2. **Logs de Operações**:
   - Registrar criação, atualização e desativação de usuários
   - Registrar alterações de perfil
   - Registrar alterações de senha

## Testes

1. **Testes Unitários**:
   - Validação de token JWT
   - Criptografia de senha
   - Validação de regras de negócio

2. **Testes de Integração**:
   - Fluxo completo de autenticação
   - Gestão de usuários
   - Controle de acesso baseado em perfil

## Implementação Inicial

Para iniciar a implementação, seguiremos esta ordem:

1. Modelo de Usuário e Repository
2. Serviço de Autenticação e UserDetails
3. Configuração JWT e Filtros de Segurança
4. Controllers de Autenticação e Usuário
5. Testes e Validação

Esta especificação servirá como guia para a implementação do módulo de usuários e autenticação JWT, garantindo que todos os requisitos de segurança e funcionalidades sejam atendidos.
