# Sistema de Fechamento de Caixa — Documentação

Esta documentação resume o estado do projeto e referencia o PRD v1.0 (fonte da verdade operacional) localizado em `backend/architecture/PRD.md`.

Links úteis:

- PRD: `backend/architecture/PRD.md`
- Arquitetura: `backend/architecture/diagrams/ARQUITETURA.md`
- Alinhamento: `backend/architecture/diagrams/ALINHAMENTO.md`
- SPA base: `backend/architecture/diagrams/SISTEMA.CENTRAL.md`

## Status do Projeto (Set/2025)

O projeto está maduro em segurança e base de dados, com Roadmap claro no PRD para evolução do módulo de Fechamentos.

### Padrões de Domínio e Arquitetura (NOVO)

Este projeto adota oficialmente DDD, eventos de domínio e camadas limpas. Regras detalhadas estão em:

- `.github/copilot-rules/project-codification.md` → seção “Domínio e DDD – Regras e Contratos (ADOTAR)”
- `.github/copilot-rules/project-rules.md` → “ADDENDUM: DDD, Eventos e Camadas (ALINHADO)”

Resumo prático:

- Aggregate Root: Fechamento governa FormasPagamento e Despesas
- Value Objects: Dinheiro, Inconsistencia, TotalCaixa (imutáveis, BigDecimal escala 2)
- Eventos: FechamentoFinalizadoEvent abastece projeções (Dashboard/Erros de Caixa)
- Strategy/Chain: ConsistencyRule + ConsistencyChecker para validações plugáveis
- DTO↔Domínio: MapStruct; controllers finos
- Hexagonal: domain/application/infrastructure/presentation

Checklist por PR (obrigatório quando tocar domínio):

- [ ] Usou VO `Dinheiro` (sem BigDecimal cru no domínio)
- [ ] Publicou `FechamentoFinalizadoEvent` no caso de uso
- [ ] Regras em `ConsistencyRule` + testes
- [ ] Consultas de Dashboard/Erros via projeções (quando existirem)
- [ ] MapStruct para mapeamento; `@Valid` nos DTOs
- [ ] Logs INFO com mascaramento de sensíveis

### Concluído

- ✅ Autenticação JWT (Bearer) com perfis: ADMIN, GERENTE, CAIXA; filtros e entrypoint configurados
- ✅ Context-path configurado: `/api` (porta padrão 8080; perfis podem usar 8081)
- ✅ Segurança de configuração: templates `.example` e `AppProperties` para `app.jwt.*`
- ✅ Base de dados MongoDB: repositórios e auto-index ativado
- ✅ Remoção de prints de debug: padronizado SLF4J
- ✅ PRD v1.0 publicado e alinhado ao código (inclui seção 20 — ajustes finos)

## Tarefas Pendentes (alinhadas ao PRD)

### Alta Prioridade — Fase A

### Roadmap Incremental para DDD/Eventos (NOVO)

1. Introduzir VOs `Dinheiro` e `Inconsistencia` no domínio (sem alterar contratos externos)
2. Extrair `ConsistencyChecker` + primeira regra `CaixaZeroRule`
3. Publicar `FechamentoFinalizadoEvent` em “finalizar fechamento”
4. Projeção `ErrosDeCaixaProjection` consumindo o evento
5. MapStruct nos mapeamentos DTO↔Domínio
6. Ajustar consultas do Dashboard para consumirem projeções (quando disponível)
7. Testes: VOs (unit), regras (unit), publicação de evento e projeções (integration)

- [ ] Implementar Fechamentos: DTOs (`FechamentoRequest`, `FechamentoResponse`), `FechamentoService`, `FechamentoController`
- [ ] Idempotência diária: endpoint `/api/fechamentos/diario`, método repo por `(usuarioId, data)`, índice `{ usuarioId: 1, data: 1 }`
- [ ] Endpoint de refresh token: `/api/auth/refresh` (utilitário já existe)
- [ ] Swagger/OpenAPI: publicar `/swagger-ui` e `/v3/api-docs` (liberar no SecurityConfig)
- [ ] CORS (se SPA/externo): restringir por origem/métodos
- [ ] Regras de cálculo com BigDecimal (2 casas) e `ContadorDinheiro` sem doubles

### Média Prioridade — Fases B/C

- [ ] Relatórios (Excel) com Apache POI; PDF opcional (Jasper)
- [ ] Comprovantes: upload multipart, validação, armazenamento e associação

### Baixa Prioridade

- [ ] Otimizações de consultas (Mongo)
- [ ] Logs de auditoria mais detalhados
- [ ] Testes automatizados abrangentes (planejados após Core)

## Alerta de Segurança (ação obrigatória)

- Foi identificado um URI do MongoDB Atlas com credenciais em `src/main/resources/application.properties`.
  - Remover do repositório (substituir por variável/placeholder)
  - Rotacionar imediatamente a senha do usuário do banco
  - Usar somente `application.properties.example` + variáveis de ambiente

## Arquitetura (resumo)

- Padrão em camadas: Controller → Service → Repository → MongoDB
- Segurança: Spring Security + JWT (Bearer). Rotas sob `/api`, exceção para `/auth/*`
- Observabilidade: SLF4J configurado; expandir logs de auditoria em fases futuras

Fluxo típico:

1. Requisição com JWT
2. Filtro valida e popula SecurityContext
3. Controller chama Service
4. Service aplica regras do PRD
5. Repository persiste/consulta
6. Resposta padronizada

## Modelos de Dados (resumo)

- Usuario: `id, email (único), nome, senhaHash(BCrypt), perfis[], ativo, dataCriacao, dataUltimoAcesso`
- FechamentoCaixa: `id, data(LocalDate), responsavel|usuarioId, caixaInicial, vendas, trocoInserido, formasPagamento{...}, despesas[], totais, consistencia, status, observacoes, metadados`
- Transacao: eventos individuais com `tipo` e `formaPagamento`, campos auxiliares por modalidade
- Caixa: sessão operacional com totais e status
- Comprovante: metadados + referência a armazenamento

Detalhes completos no PRD (seção 5).

## Regras de Negócio (resumo)

- Consistência: `abs(delta) <= 0,01`
- SANGRIA é saída (–), REFORÇO é entrada (+)
- Estados (API): ABERTO → FECHADO → CONFERIDO. Mapeamento do domínio atual segue PRD 10.2
- Idempotência diária por `(usuarioId, data)` (ou `responsavel, data`): retornar existente

## Endpoints (resumo — ver PRD seção 7)

- Auth: `POST /api/auth/login`, `POST /api/auth/refresh`
- Usuários: `GET /api/usuarios`, `GET /api/usuarios/{id}`, `POST /api/usuarios` (ADMIN), `PUT /api/usuarios/{id}`, `PATCH /api/usuarios/{id}/senha`, `PATCH /api/usuarios/{id}/ativar|desativar`
- Fechamentos: `GET /api/fechamentos`, `GET /api/fechamentos/{id}`, `POST /api/fechamentos`, `PUT /api/fechamentos/{id}`, `PATCH /api/fechamentos/{id}/fechar`, `PATCH /api/fechamentos/{id}/conferir`, `POST /api/fechamentos/{id}/validar`, `POST /api/fechamentos/diario`
- Comprovantes: `POST /api/comprovantes` (multipart), `GET /api/comprovantes/{id}`
- Relatórios: `GET /api/relatorios/fechamentos?dataInicio&dataFim&formato=excel|pdf`

## Segurança (boas práticas)

- Todas as rotas (exceto login/registrar, se ativo) exigem Bearer
- Senhas com BCrypt; sem prints em produção; logs de tentativas de autenticação
- CORS restrito quando houver cliente externo
- Segredos somente via env/secrets do ambiente; `.env` nunca commitado

## Observações Finais

- O PRD v1.0 é a referência central para contratos, regras e roadmap. Após cada incremento, atualizar Swagger/OpenAPI e, se necessário, esta documentação.
