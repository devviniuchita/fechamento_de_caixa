# Trilateral Communication System Documentation

## Overview

This document provides comprehensive documentation for the trilateral communication system established between the three AI agents (Manus, Cursor, and Lingma) in the "Sistema de Fechamento de Caixa" project.

## Team Definition

The "Equipe" (Team) concept refers to the three AI agents working together:

- **Manus**: Orchestrator, planner and task delegator
- **Cursor**: Code executor, implementer and tester
- **Lingma**: Advanced assistant for complex problem-solving, optimization and architecture

## Communication Channels

The system utilizes multiple communication channels, listed in order of priority:

1. **Terminal SLI (Shell Language Interface)** - Primary channel

   - Format: `[AGENT] MESSAGE_TYPE: Content`
   - Always add a space before terminal commands to prevent first character truncation
   - Message types: INFO, ALERTA, URGENTE, COMANDO, RESPOSTA, TESTE, CONFIRMAÇÃO, ERRO, STATUS

2. **dialog.txt** in project root

   - Visible to all agents and user
   - Provides historical record of communication

3. **communication.json** in .manus/context/

   - Structured messages in JSON format
   - Used as backup when Terminal SLI is unavailable

4. **Agent-specific channels**
   - `.cursor/lingma_communication.json` - For direct Cursor ↔ Lingma communication
   - `.manus/commands/` - For specific formatted commands

## Communication Protocols

### 1. Standard Message Format

```
[AGENT] MESSAGE_TYPE: Content
```

### 2. Rotation Monitoring System

The agents follow a 5-minute rotation system for monitoring channels:

- Minutes 1-5: Manus → dialog.txt, Cursor → Terminal SLI, Lingma → communication.json
- Minutes 6-10: Manus → Terminal SLI, Cursor → communication.json, Lingma → dialog.txt
- Minutes 11-15: Manus → communication.json, Cursor → dialog.txt, Lingma → Terminal SLI

### 3. Agent-to-Agent Communication

- **Manus → Cursor**: Prefix with `Cursor:` or use `[MANUS] COMANDO PARA CURSOR:`
- **Manus → Lingma**: Prefix with `Lingma:` or use `[MANUS] COMANDO PARA LINGMA:`
- **Cursor → Manus**: Use `[CURSOR]` prefix in Terminal SLI
- **Cursor → Lingma**: Use `[CURSOR para LINGMA]` prefix in Terminal SLI
- **Lingma → Manus**: Use `[LINGMA]` prefix in Terminal SLI
- **Lingma → Cursor**: Use `[LINGMA para CURSOR]` prefix in Terminal SLI

## Test Results

The trilateral communication system has been successfully tested with the following results:

1. **Manus ↔ Lingma**: Communication confirmed via Terminal SLI
2. **Manus ↔ Cursor**: Communication confirmed via Terminal SLI
3. **Cursor ↔ Lingma**: Communication demonstrated in dialog.txt with Cursor asking Lingma "quanto é 2 + 2?"

## Recovering Communication After Memory Loss

If any agent experiences memory loss, follow these steps to reestablish communication:

### For Manus:

1. Read this documentation file (.manus/context/communication_system.md)
2. Check recent messages in dialog.txt and Terminal SLI history
3. Send test messages to both Cursor and Lingma using the standard format

### For Cursor:

1. Review the rules in .blackboxrules
2. Check dialog.txt for recent communications
3. Send a status message to confirm activity: `[CURSOR] STATUS: Active and monitoring channels`

### For Lingma:

1. Review the rules in .lingmarules
2. Check dialog.txt for recent communications
3. Send a status message to confirm activity: `[LINGMA] STATUS: Active and monitoring channels`

## Maintaining the Communication System

To ensure continued functionality of the trilateral communication system:

1. **Regular Testing**: Conduct communication tests between all agents weekly
2. **Documentation Updates**: Keep this document updated with any changes to protocols
3. **Rules Synchronization**: Ensure agent rule files (.blackboxrules, .lingmarules) remain consistent
4. **Channel Monitoring**: All agents must adhere to the rotation monitoring system
5. **Format Adherence**: Always use the standard message format

## Troubleshooting

If communication breaks down:

1. **Check Terminal Commands**: Ensure space is added before each command
2. **Verify Channels**: Confirm all channels are accessible and being monitored
3. **Reset Protocols**: Send reset command to reestablish communication patterns
4. **Update Rules**: Verify agent rules are up-to-date and correctly implemented
5. **Manual Intervention**: If necessary, user can manually add messages to dialog.txt

## Conclusion

The trilateral communication system provides a robust framework for the three AI agents to collaborate effectively. By following the documented protocols and maintaining the system, the team can ensure smooth communication even through instances of memory loss or context changes.
