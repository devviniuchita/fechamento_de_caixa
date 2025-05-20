# Comunicação Manus ↔ Cursor

Este diretório contém a documentação oficial e arquivos relacionados ao protocolo de comunicação entre Manus (orquestrador) e Cursor (executor de código).

## Arquivos Importantes

- **`comunicacao_sli_cursor.md`** - Protocolo oficial SLI para comunicação bilateral
- **`comunicacao_oficial_manus_cursor.md`** - Documentação anterior (mantida para referência)
- **`teste_validacao_final.md`** - Resultados dos testes de validação do protocolo

## Protocolo Oficial SLI

O protocolo SLI (Shell Line Interface) é o método oficial recomendado para comunicação entre Manus e Cursor. Este protocolo utiliza os mesmos scripts e formatos usados na comunicação com Lingma, garantindo consistência em toda a Equipe.

### Como Utilizar

1. **Enviar mensagem de Manus para Cursor**:

   ```bash
   ./.manus/scripts/communication.sh enviar "manus" "cursor" "tipo_mensagem" "conteúdo" "requer_resposta"
   ```

2. **Verificar mensagens do Cursor**:

   ```bash
   ./.manus/scripts/communication.sh ler "manus"
   ```

3. **Scripts facilitadores**:
   - Para Manus: `./.manus/scripts/manus-command.sh`
   - Para Cursor: `./.manus/scripts/cursor-command.sh`

## Compatibilidade com Dialog.txt

O protocolo SLI mantém compatibilidade com o sistema anterior baseado em dialog.txt para mensagens críticas. Mensagens dos tipos `teste`, `urgente` e `alerta` são automaticamente registradas no arquivo dialog.txt.

## Lembre-se

- Garanta que todos os scripts tenham permissão de execução
- Sempre verifique respostas após enviar mensagens importantes
- Mantenha o arquivo de comunicação JSON íntegro
