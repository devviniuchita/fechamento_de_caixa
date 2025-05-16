# Sistema de Fechamento de Caixa - Documentação Técnica

## 1. Visão Geral

### 1.1 Propósito

Sistema empresarial para gerenciamento completo de fechamento de caixa, oferecendo controle preciso de operações financeiras, gestão de comprovantes, relatórios gerenciais e backup automatizado.

### 1.2 Stack Tecnológica

- **Backend**:
  - Spring Boot 2.7.18
  - Java 17
  - Maven
  - Jakarta Validation
  - Hibernate Validator
  - Lombok
- **Banco de Dados**:
  - MongoDB 4.4+
- **Segurança**:
  - Spring Security
  - JWT (JSON Web Tokens)
  - BCrypt para senhas
- **Interfaces**:
  - Desktop: JavaFX 17.0.2
  - Web: Thymeleaf + Bootstrap
- **Integrações**:
  - Google Drive API (armazenamento/backup)
  - Apache POI (relatórios Excel)

## 2. Arquitetura

### 2.1 Organização de Pacotes

```
com.controle.fechamentocaixa/
├── config/           # Configurações Spring Boot e MongoDB
├── controller/       # Controllers REST e endpoints da API
├── dto/             # DTOs (CashClosing, Receipt, etc)
├── exception/       # Tratamento de exceções e erros
├── frontend/        # Interface desktop JavaFX
│   ├── controller/  # Controladores JavaFX
│   ├── service/     # Serviços específicos do frontend
│   └── util/        # Utilitários da interface
├── model/           # Entidades do domínio com validações
├── repository/      # Repositórios MongoDB
├── security/        # Configuração JWT e autenticação
└── service/         # Regras de negócio e integrações
    └── impl/        # Implementações dos serviços
```

### 2.2 Camadas e Responsabilidades

#### 2.2.1 Controllers (`controller/`)

- Exposição de endpoints REST
- Validação com Jakarta Validation
- Controle de acesso baseado em roles
- Tratamento global de exceções
- Documentação Swagger/OpenAPI

Exemplo (CashClosingController):

```java
@RestController
@RequestMapping("/api/cash-closings")
@Validated
public class CashClosingController {
    @PostMapping
    @PreAuthorize("hasAnyRole('CAIXA', 'GERENTE', 'ADMIN')")
    public ResponseEntity<CashClosingDTO> create(
        @Valid @RequestBody CashClosingDTO dto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // Implementação
    }
}
```

#### 2.2.2 Services (`service/`)

- Lógica de negócio
- Validações complexas
- Orquestração de operações
- Transações
- Integração com Google Drive

Exemplo (CashClosingService):

```java
@Service
@Validated
public class CashClosingServiceImpl implements CashClosingService {
    @Transactional
    public CashClosing create(CashClosingDTO dto) {
        validateBalances(dto);
        CashClosing cashClosing = mapper.toEntity(dto);
        uploadReceipts(cashClosing);
        notifyManagers(cashClosing);
        return repository.save(cashClosing);
    }
}
```

#### 2.2.3 Repositories (`repository/`)

- Interfaces MongoDB
- Queries customizadas
- Índices e otimizações
- Agregações

Exemplo (CashClosingRepository):

```java
@Repository
public interface CashClosingRepository extends MongoRepository<CashClosing, String> {
    @Query("{'dataHora': {$gte: ?0, $lte: ?1}}")
    List<CashClosing> findByPeriod(LocalDateTime inicio, LocalDateTime fim);

    @Aggregation(pipeline = {
        "{'$match': {'status': 'FECHADO'}}",
        "{'$group': {'_id': '$operador', 'total': {'$sum': '$valorTotal'}}}"
    })
    List<OperatorTotalDTO> getTotalsByOperator();
}
```

#### 2.2.4 Models (`model/`)

- Documentos MongoDB
- Validações Jakarta
- Lombok para redução de boilerplate
- Auditoria

Exemplo (CashClosing):

```java
@Data
@Document(collection = "cash_closings")
public class CashClosing {
    @Id
    private String id;

    @NotNull(message = "Saldo inicial é obrigatório")
    private BigDecimal saldoInicial;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;

    @CreatedBy
    private String criadoPor;
}
```

#### 2.2.5 DTOs (`dto/`)

- Transferência de dados
- Validações Jakarta
- Conversões e mapeamentos
- Documentação

Exemplo (CashClosingDTO):

```java
@Data
public class CashClosingDTO {
    @NotNull(message = "Saldo inicial é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo inicial deve ser positivo")
    private BigDecimal saldoInicial;

    @Valid
    private List<PaymentMethodDTO> formasPagamento;

    @Size(max = 255, message = "Observação não pode exceder 255 caracteres")
    private String observacao;
}
```

## 3. Módulos do Sistema

### 3.1 Usuários e Autenticação

- **Funcionalidades**:
  - Cadastro e gestão de usuários
  - Autenticação JWT com refresh token
  - Autorização por roles
  - Auditoria de ações
- **Roles**:
  - ADMIN: Acesso total
  - GERENTE: Conferência e relatórios
  - CAIXA: Operações básicas
- **Fluxo de Autenticação**:
  1. Login com credenciais
  2. Validação e geração de JWT
  3. Refresh token para renovação
  4. Validação de permissões
  5. Registro de auditoria

### 3.2 Fechamento de Caixa

- **Operações**:
  - Abertura de caixa com validação
  - Registro de movimentações em tempo real
  - Fechamento com dupla conferência
  - Sangrias e suprimentos com aprovação
- **Validações**:
  - Saldo inicial vs final
  - Totais por forma de pagamento
  - Comprovantes obrigatórios
  - Consistência de horários
- **Fluxo de Fechamento**:
  1. Registro de movimentações
  2. Upload de comprovantes
  3. Validações automáticas
  4. Conferência por superior
  5. Geração de relatório
  6. Backup no Google Drive

### 3.3 Gestão de Comprovantes

- **Funcionalidades**:
  - Upload multi-arquivo
  - Validação de formato e tamanho
  - Compressão automática
  - Armazenamento no Drive
  - Backup redundante
- **Formatos Suportados**:
  - JPG/JPEG (max 5MB)
  - PNG (max 5MB)
  - PDF (max 10MB)
- **Fluxo de Upload**:
  1. Validação do arquivo
  2. Compressão se necessário
  3. Upload para Google Drive
  4. Geração de backup
  5. Vinculação ao fechamento

### 3.4 Relatórios

- **Tipos**:
  - Diário com detalhamento
  - Semanal consolidado
  - Mensal com gráficos
  - Por operador com métricas
  - Inconsistências e ajustes
- **Formatos**:
  - Excel (XLSX) com formatação
  - PDF para impressão
  - CSV para exportação
- **Características**:
  - Gráficos dinâmicos
  - Filtros avançados
  - Exportação automática
  - Envio por email
  - Agendamento de geração

### 3.5 Backup e Recuperação

- **Estratégias**:
  - Backup incremental diário
  - Backup completo semanal
  - Replicação MongoDB
  - Versionamento de documentos
- **Locais de Armazenamento**:
  - Google Drive (documentos)
  - MongoDB Atlas (dados)
  - Servidor local (logs)
- **Recuperação**:
  - Point-in-time recovery
  - Restauração seletiva
  - Verificação de integridade

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
