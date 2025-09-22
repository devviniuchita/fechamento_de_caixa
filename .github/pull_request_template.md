# PR – Fechamento de Caixa

## Objetivo

Descreva o problema/feature e o que este PR resolve.

## Checklist de Conformidade (DDD/Eventos/VO/Tests)

- [ ] VO `Dinheiro` usado em cálculos monetários (sem BigDecimal cru no domínio)
- [ ] Evento de domínio publicado (`FechamentoFinalizadoEvent`, quando aplicável)
- [ ] Regras de consistência implementadas como `ConsistencyRule` e cobertas por testes
- [ ] Consultas de Dashboard/Erros consomem projeções (quando existirem)
- [ ] MapStruct para mapeamento DTO↔Domínio; controllers finos
- [ ] `@Valid` nas entradas; tratamento de erros padronizado
- [ ] Logs INFO com mascaramento de dados sensíveis
- [ ] Testes unitários e/ou de integração atualizados (VOs, regras, eventos, projeções)

## Notas de Implementação

- Pontos de decisão e trade-offs:
- Riscos e mitigação:
- Migrações/índices necessários:

## Evidências

- [ ] Resultados de testes
- [ ] Prints/telas relevantes

## Impactos

- [ ] API pública
- [ ] Segurança
- [ ] Migração de dados

## Tarefas Relacionadas

- Issue/Task:
