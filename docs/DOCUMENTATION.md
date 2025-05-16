# Sistema de Fechamento de Caixa - Documentação Técnica

## 1. Visão Geral

### 1.1 Propósito

Sistema empresarial para gerenciamento completo de fechamento de caixa, oferecendo controle preciso de operações financeiras, gestão de comprovantes, relatórios gerenciais e backup automatizado.

### 1.2 Stack Tecnológica

- **Backend**:
  - Spring Boot 2.7.18
  - Java 17
  - Maven
- **Banco de Dados**:
  - MongoDB 4.4+
- **Segurança**:
  - Spring Security
  - JWT (JSON Web Tokens)
- **Interfaces**:
  - Desktop: JavaFX
  - Web: Thymeleaf + Bootstrap
- **Integrações**:
  - Google Drive API (armazenamento/backup)
  - Apache POI (relatórios Excel)

## 2. Arquitetura

### 2.1 Organização de Pacotes

```
com.seucodigo.fecharcaixa/
├── config/           # Configurações (Spring, Security, MongoDB)
├── controller/       # Controllers REST
├── dto/             # Objetos de transferência de dados
├── model/           # Entidades do MongoDB
├── repository/      # Interfaces MongoRepository
├── service/         # Lógica de negócio
├── security/        # Implementação JWT
├── util/           # Classes utilitárias
├── exception/       # Exceções customizadas
└── validation/      # Validadores
```

### 2.2 Camadas e Responsabilidades

#### 2.2.1 Controllers (`controller/`)

- Exposição de endpoints REST
- Validação básica de requisições
- Controle de acesso por role
- Tratamento inicial de erros

Exemplo (CashClosingController):

```java
@RestController
@RequestMapping("/api/cash-closings")
public class CashClosingController {
    @PostMapping
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosingDTO> create(@Valid @RequestBody CashClosingDTO dto) {
        // Implementação
    }
}
```

#### 2.2.2 Services (`service/`)

- Lógica de negócio
- Validações complexas
- Orquestração de operações
- Transações

Exemplo (CashClosingService):

```java
@Service
public class CashClosingService {
    public CashClosing create(CashClosingDTO dto) {
        validateBalances(dto);
        // Lógica de criação
        notifyManagers(cashClosing);
        return cashClosing;
    }
}
```

#### 2.2.3 Repositories (`repository/`)

- Interfaces MongoDB
- Queries customizadas
- Índices e otimizações

Exemplo (CashClosingRepository):

```java
@Repository
public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    List<CashClosing> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}
```

#### 2.2.4 Models (`model/`)

- Documentos MongoDB
- Validações JPA
- Relacionamentos

Exemplo (CashClosing):

```java
@Document(collection = "cash_closings")
public class CashClosing {
    @Id
    private String id;

    @NotNull
    private BigDecimal saldoInicial;
    // Outros campos
}
```

#### 2.2.5 DTOs (`dto/`)

- Transferência de dados
- Validações
- Conversões

Exemplo (CashClosingDTO):

```java
public class CashClosingDTO {
    @NotNull(message = "Saldo inicial é obrigatório")
    private BigDecimal saldoInicial;
    // Outros campos e validações
}
```

## 3. Módulos do Sistema

### 3.1 Usuários e Autenticação

- **Funcionalidades**:
  - Cadastro e gestão de usuários
  - Autenticação JWT
  - Autorização por roles
- **Roles**:
  - ADMIN: Acesso total
  - GERENTE: Conferência e relatórios
  - CAIXA: Operações básicas
- **Fluxo de Autenticação**:
  1. Login com credenciais
  2. Geração de JWT
  3. Uso do token nas requisições
  4. Validação de permissões

### 3.2 Fechamento de Caixa

- **Operações**:
  - Abertura de caixa
  - Registro de movimentações
  - Fechamento com conferência
  - Sangrias e suprimentos
- **Validações**:
  - Saldo inicial vs final
  - Totais por forma de pagamento
  - Comprovantes obrigatórios
- **Fluxo de Fechamento**:
  1. Registro de movimentações
  2. Upload de comprovantes
  3. Conferência por superior
  4. Geração de relatório

### 3.3 Gestão de Comprovantes

- **Funcionalidades**:
  - Upload de imagens
  - Validação de formato
  - Armazenamento no Drive
  - Vinculação ao fechamento
- **Formatos Suportados**:
  - JPG/JPEG
  - PNG
  - PDF
- **Fluxo de Upload**:
  1. Validação do arquivo
  2. Upload para Google Drive
  3. Armazenamento da referência
  4. Vinculação ao fechamento

### 3.4 Relatórios

- **Tipos**:
  - Diário
  - Semanal
  - Mensal
  - Por operador
- **Formatos**:
  - Excel (XLSX)
  - PDF
- **Conteúdo**:
  - Movimentações
  - Totalizadores
  - Comprovantes
  - Inconsistências

### 3.5 Backup e Integração Drive

- **Funcionalidades**:
  - Backup automático diário
  - Armazenamento de comprovantes
  - Organização por data
- **Configurações**:
  - Credenciais Google
  - Pasta destino
  - Agendamento

## 4. Interfaces

### 4.1 Desktop (JavaFX)

- **Telas**:
  - Login
  - Dashboard
  - Operações de caixa
  - Upload de comprovantes
  - Visualização rápida
- **Características**:
  - Modo offline
  - Impressão local
  - Atalhos de teclado

### 4.2 Web (Thymeleaf)

- **Páginas**:
  - Dashboard gerencial
  - Gestão de usuários
  - Relatórios
  - Configurações
- **Características**:
  - Responsivo
  - Temas dark/light
  - Exportação direta

## 5. Segurança e Boas Práticas

### 5.1 Autenticação e Autorização

- JWT com expiração
- Refresh tokens
- CORS configurado
- Roles e permissões

### 5.2 Validações

- Bean Validation
- Validações customizadas
- Tratamento de exceções
- Mensagens claras

### 5.3 Logging e Monitoramento

- Logs estruturados
- Auditoria de operações
- Métricas de performance
- Alertas de inconsistências

## 6. Roadmap de Implementação

### Fase 1: Core e Segurança (2-3 semanas)

1. Setup inicial Spring Boot
2. Configuração MongoDB
3. Implementação JWT
4. CRUD básico

### Fase 2: Módulo Principal (2-3 semanas)

1. Fechamento de caixa
2. Upload de comprovantes
3. Validações de negócio
4. Testes unitários

### Fase 3: Interfaces (3-4 semanas)

1. Interface JavaFX
2. Templates Thymeleaf
3. Responsividade
4. Testes de usabilidade

### Fase 4: Integrações (2-3 semanas)

1. Google Drive API
2. Apache POI
3. Backup automático
4. Testes de integração

### Fase 5: Qualidade (2 semanas)

1. Documentação Swagger
2. Logging
3. Monitoramento
4. Performance

## 7. Considerações de Desenvolvimento

### 7.1 Padrões de Código

- Clean Code
- SOLID
- DRY
- Comentários significativos

### 7.2 Versionamento

- Git Flow
- Commits semânticos
- Code review
- CI/CD

### 7.3 Testes

- Unitários (JUnit)
- Integração (TestContainers)
- E2E (Selenium)
- Cobertura > 80%

## 8. Próximos Passos

1. Implementar tratamento global de exceções
2. Desenvolver interface web básica
3. Configurar integração com Google Drive
4. Implementar geração de relatórios
5. Criar testes automatizados
6. Documentar APIs com Swagger
