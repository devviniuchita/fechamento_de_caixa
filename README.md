# Sistema de Fechamento de Caixa

Sistema para gerenciamento e fechamento de caixa com suporte a múltiplos tipos de pagamento, geração de relatórios e backup automático.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 2.7.18
- MongoDB
- Spring Security + JWT
- JavaFX
- Thymeleaf
- Google Drive API
- Apache POI

## Requisitos

- JDK 17
- MongoDB 4.4+
- Maven 3.6+
- Node.js 18+ (para interface web)

## Configuração

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/fechamento-de-caixa.git
cd fechamento-de-caixa
```

2. Configure o MongoDB:

- Instale o MongoDB
- Crie um banco de dados chamado `fechamento_caixa`
- O sistema irá criar as coleções automaticamente

3. Configure as variáveis de ambiente (opcional):

```bash
export JWT_SECRET=seu_secret_jwt_aqui
export JWT_EXPIRATION=86400000
```

4. Configure as credenciais do Google Drive (para backup):

- Crie um projeto no Google Cloud Console
- Habilite a Google Drive API
- Baixe as credenciais e salve como `credentials.json` em `src/main/resources/`

## Compilação e Execução

1. Compile o projeto:

```bash
mvn clean install
```

2. Execute a aplicação:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

- API REST: http://localhost:8080/api
- Interface Web: http://localhost:8080/api/web
- Interface JavaFX: Execute a classe `JavaFXApplication`

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/seucodigo/fecharcaixa/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   └── resources/
│       ├── static/
│       ├── templates/
│       └── application.yml
```

## Funcionalidades

- [x] Autenticação e Autorização

  - Login/Logout
  - Roles (ADMIN, GERENTE, CAIXA)
  - JWT Token

- [x] Gestão de Usuários

  - CRUD completo
  - Alteração de senha
  - Controle de acesso

- [x] Fechamento de Caixa

  - Registro de operações
  - Múltiplos tipos de pagamento
  - Conferência e aprovação

- [ ] Comprovantes

  - Upload de imagens
  - Armazenamento no Google Drive
  - Vinculação com fechamento

- [ ] Relatórios

  - Diários
  - Mensais
  - Por operador
  - Exportação Excel

- [ ] Backup
  - Automático
  - Google Drive
  - Restauração

## API Endpoints

### Autenticação

- POST `/api/auth/login` - Login
- POST `/api/auth/refresh` - Renovar token

### Fechamento de Caixa

- POST `/api/cash-closings` - Criar fechamento
- GET `/api/cash-closings/{id}` - Buscar fechamento
- PUT `/api/cash-closings/{id}` - Atualizar fechamento
- DELETE `/api/cash-closings/{id}` - Excluir fechamento

### Relatórios

- GET `/api/cash-closings/reports/daily/{date}` - Relatório diário
- GET `/api/cash-closings/reports/weekly/{startDate}` - Relatório semanal
- GET `/api/cash-closings/reports/monthly/{month}` - Relatório mensal

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Crie um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
