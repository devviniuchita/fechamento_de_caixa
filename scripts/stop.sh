#!/usr/bin/env bash
set -euo pipefail

# Porta padrão
PORT="${1:-8080}"

echo "[INFO] Tentando encerrar processos na porta ${PORT}..."

# Usa PowerShell para encontrar e matar o processo que escuta a porta
if command -v powershell.exe >/dev/null 2>&1; then
  set +e
  OUT=$(powershell.exe -NoProfile -Command "try { \$pids = (Get-NetTCPConnection -LocalPort ${PORT} -State Listen -ErrorAction Stop).OwningProcess; if (\$pids) { \$pids | ForEach-Object { Stop-Process -Id \$_ -Force -ErrorAction SilentlyContinue }; Write-Output 'Killed: ' + (\$pids -join ','); } else { Write-Output 'No listener on port.' } } catch { Write-Output 'No listener on port.' }" | tr -d '\r')
  STATUS=$?
  set -e
  echo "$OUT"
else
  # Fallback: tenta netstat + taskkill via cmd
  cmd.exe /c "for /f \"tokens=5\" %a in ('netstat -ano ^| findstr :%PORT% ^| findstr LISTENING') do taskkill /F /PID %a" || true
fi

echo "[INFO] Encerramento solicitado."
