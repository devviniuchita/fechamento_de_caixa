# Módulo de Fechamento de Caixa

## Visão Geral

Responsável por gerenciar a rotina de abertura, fechamento e conferência de caixas.

## Domínio (DDD)

- Agregado: **Fechamento** (root)
- Value Objects: **Dinheiro**, **FormasPagamento**, **Despesa**, **Inconsistencia** (SOBRA|FALTA|ZERADO)
- Eventos: **FechamentoFinalizadoEvent** (publicado ao concluir)
- Projeções (read models): **DashboardProjection**, **ErrosDeCaixaProjection**

### Fechamento (atributos mínimos)

- id, data(LocalDate), responsavel/usuarioId
- caixaInicial(Dinheiro), vendas(Dinheiro), trocoInserido(Dinheiro)
- formasPagamento(FormasPagamento), despesas(List<Despesa>)
- totalCaixa(Dinheiro) calculado
- inconsistencia(Inconsistencia)
- status: ABERTO → FECHADO → CONFERIDO

### Regras

- Cálculo com BigDecimal (escala 2, HALF_UP)
- Consistência: `abs(totalCaixa) <= 0,01` → ZERADO (correto); senão SOBRA/FALTA
- Validações plugáveis via `ConsistencyRule` + `ConsistencyChecker`

## Regras de Negócio

- Calcular valor final automaticamente
- Validar consistência dos valores
- Restringir ações por perfil (ADMIN, GERENTE, CAIXA)
- Permitir conferência apenas após fechamento

## Projeções e Índices

- Projeção `erros_de_caixa`: documentos com { fechamentoId, data, responsavel, vendas, totalCaixa, motivo }
- Índices recomendados:
  - `fechamentos`: `{ usuarioId: 1, data: 1 }` (idempotência diária)
  - `erros_de_caixa`: `{ data: -1 }`, `{ responsavel: 1, data: -1 }`

## Endpoints Esperados

- `GET /fechamentos` — listar todos
- `GET /fechamentos/{id}` — buscar por ID
- `POST /fechamentos` — criar fechamento
- `PUT /fechamentos/{id}` — atualizar
- `PATCH /fechamentos/{id}/fechar` — fechar caixa
- `PATCH /fechamentos/{id}/conferir` — conferir fechamento
- `DELETE /fechamentos/{id}` — excluir fechamento

### Endpoints para Projeções (consulta)

- `GET /projecoes/erros-caixa?dataInicio&dataFim&responsavel` — listar inconsistências (fonte: `erros_de_caixa`)
