# Estrutura Detalhada do Projeto: Fechamento de Caixa

## Visão Geral

Este documento fornece um mapa completo da estrutura de arquivos e diretórios do projeto, servindo como uma referência central para todos os desenvolvedores. O objetivo é manter a organização, evitar redundâncias e garantir que todos compreendam o propósito de cada componente do sistema.

A estrutura foi gerada e analisada utilizando ferramentas modernas de CLI para garantir precisão, ignorando arquivos e pastas não relevantes para o desenvolvimento (como `target/`, `.git/`, `.vscode/`, etc.).

---

## Árvore de Arquivos e Diretórios

```
.
├── application.yml.example
├── backend
│   ├── architecture
│   │   ├── diagrams
│   │   │   ├── ALINHAMENTO.md
│   │   │   ├── ARQUITETURA.md
│   │   │   └── SISTEMA.CENTRAL.md
│   │   └── modules
│   │       └── fechamento.md
│   ├── commands
│   │   ├── AUTENTICACAO_JWT.md
│   │   └── templates
│   ├── config
│   ├── instructions.md
│   ├── PLANO.DE.ACAO.md
│   └── tasks.json
├── docs
│   └── DOCUMENTATION.md
├── index.html
├── LICENSE.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── public
│   ├── css
│   │   └── style.css
│   └── js
│       ├── app.js
│       └── server.js
├── run-app.bat
├── scripts
│   └── validate_manus_fix.bat
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── controle
    │   │           └── fechamentocaixa
    │   │               ├── config
    │   │               ├── controller
    │   │               ├── dto
    │   │               ├── exception
    │   │               ├── model
    │   │               ├── repository
    │   │               ├── security
    │   │               └── service
    │   └── resources
    │       └── application.properties.example
    └── test
        ├── java
        │   └── com
        │       └── controle
        │           └── fechamentocaixa
        │               ├── config
        │               └── repository
        └── resources
            ├── application-test.properties
            └── application.properties
```

---

## Descrição dos Diretórios Principais

- **`/` (Raiz)**
  - Contém arquivos de configuração do projeto (`pom.xml`, `mvnw`), a base do frontend original (`index.html`, `public/`) e scripts de execução (`run-app.bat`).
  - `application.yml.example`: Template para configuração de ambiente.

- **`backend/`**
  - **Propósito**: Centraliza toda a documentação, planejamento e metadados do projeto. **Não contém código-fonte da aplicação**.
  - `architecture/`: Documentos que descrevem a arquitetura do sistema, incluindo diagramas, mapeamentos e a estrutura do projeto (este arquivo).
  - `commands/`: Especificações detalhadas para módulos e funcionalidades, como `AUTENTICACAO_JWT.md`.
  - `docs/`: Documentação geral do projeto, como o `DOCUMENTATION.md` que resume o status e as tarefas.
  - `PLANO.DE.ACAO.md`: O plano de desenvolvimento macro, com fases e sprints.
  - `tasks.json`: Definição das tarefas para gerenciamento de sprints.

- **`src/main/java/com/controle/fechamentocaixa/`**
  - **Propósito**: Coração da aplicação Spring Boot. Contém todo o código-fonte Java.
  - `config/`: Classes de configuração do Spring, como `SecurityConfig`.
  - `controller/`: Controladores REST que expõem os endpoints da API.
  - `dto/`: _Data Transfer Objects_, usados para modelar as requisições e respostas da API.
  - `exception/`: Classes de exceção customizadas para tratamento de erros.
  - `model/`: As entidades do domínio, mapeadas para os documentos do MongoDB.
  - `repository/`: Interfaces do Spring Data MongoDB para acesso ao banco de dados.
  - `security/`: Classes relacionadas à segurança com JWT, como filtros e serviços de usuário.
  - `service/`: Onde reside a lógica de negócio da aplicação.

- **`src/main/resources/`**
  - **Propósito**: Armazena arquivos de configuração e recursos que não são código Java.
  - `application.properties.example`: Template de configuração para o Spring Boot.

- **`src/test/`**
  - **Propósito**: Contém o código-fonte para os testes unitários e de integração. A estrutura de pacotes espelha a do código principal para facilitar a organização.
  - `resources/`: Arquivos de configuração específicos para o ambiente de teste, como `application-test.properties`.

- **`public/`**
  - **Propósito**: Contém os arquivos estáticos (CSS, JS) da aplicação frontend original, que serve como base e inspiração para a nova arquitetura.

---

## Conclusão

Esta estrutura modular e bem definida permite um desenvolvimento organizado e escalável. Ao adicionar novas funcionalidades, devemos sempre seguir esta organização, criando classes nos pacotes apropriados e atualizando a documentação relevante na pasta `backend/`.
