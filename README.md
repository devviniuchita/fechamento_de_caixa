# Sistema de Fechamento de Caixa

Sistema para gerenciamento e controle de fechamento de caixa com interface desktop (JavaFX) e web.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- JavaFX 17.0.2
- MongoDB
- Spring Security com JWT
- Google Drive API para backup

## Pré-requisitos

## Configuração

1. Clone o repositório:

```bash
git clone
```

2. Configure as variáveis de ambiente:

```bash

# JWT

# Google Drive

```

3. Execute o MongoDB:

```bash
mongod --dbpath /path/to/data/db
```

4. Compile e execute o projeto:

```bash
mvn clean install
mvn spring-boot:run
```

## Estrutura do Projeto

```
/
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

## Funcionalidades

## Desenvolvimento

Para executar em modo de desenvolvimento:

```bash
# Backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend Web (em desenvolvimento)

```

## Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify -P integration-test

# Cobertura de código
mvn jacoco:report
```

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/amazing-feature`)
3. Commit suas mudanças (`git commit -m 'Add some amazing feature'`)
4. Push para a branch (`git push origin feature/amazing-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE.md](LICENSE.md) para detalhes.

## Contato

Seu Nome - [@seu-twitter](https://twitter.com/seu-twitter) - email@example.com

Link do projeto: [https://github.com/seu-usuario/](https://github.com/seu-usuario/)
