#!/usr/bin/env bash
# Modo seguro + anti-truncation (comandos curtos e em etapas)
set -euo pipefail

# 1) Diretório do projeto (curto e confiável)
PROJECT_DIR="$(cd "$(dirname "$0")"/.. && pwd)"
cd "$PROJECT_DIR"

# 2) JAVA_HOME e PATH (Git Bash precisa disso)
JAVA_HOME_WIN="C:\\Program Files\\Java\\jdk-24"
if command -v cygpath >/dev/null 2>&1; then
  JAVA_HOME="$(cygpath -u "$JAVA_HOME_WIN")"
else
  JAVA_HOME="/c/Program Files/Java/jdk-24"
fi
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

# 3) Verificações rápidas
java -version >/dev/null 2>&1 || { echo "❌ Java não encontrado no PATH"; exit 1; }

# 4) Config
PORT=8080
PROFILE=dev
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"
BUILD_LOG="$LOG_DIR/build.log"
RUN_LOG="$LOG_DIR/app.log"
: > "$BUILD_LOG"
: > "$RUN_LOG"

echo "[INFO] Empacotando aplicação (sem testes)..."
# 5) Empacotar JAR usando o wrapper do Maven via cmd.exe (mais estável no Windows)
#    Importante: não passar JAVA_HOME Unix para cmd.exe (causa erro). Desexportar temporariamente.
PROJECT_DIR_WIN="$PROJECT_DIR"
if command -v cygpath >/dev/null 2>&1; then
  PROJECT_DIR_WIN="$(cygpath -w "$PROJECT_DIR")"
fi

SAVED_JAVA_HOME="${JAVA_HOME:-}"
unset JAVA_HOME || true
cmd.exe /c "cd /d $PROJECT_DIR_WIN && set \"JAVA_HOME=C:\\Program Files\\Java\\jdk-24\" && mvnw.cmd -q -DskipTests package" >>"$BUILD_LOG" 2>&1 || {
  echo "❌ Falha no build. Veja $BUILD_LOG"; tail -n 120 "$BUILD_LOG"; exit 1;
}
export JAVA_HOME="$SAVED_JAVA_HOME"

# 6) Encontrar o JAR gerado (primeiro .jar em target)
JAR_FILE=""
if ls -1 target/*.jar >/dev/null 2>&1; then
  JAR_FILE="$(ls -1 target/*.jar | head -n1)"
fi

if [ -z "$JAR_FILE" ]; then
  echo "❌ Nenhum JAR encontrado em target/. Veja $BUILD_LOG"
  exit 1
fi

echo "[INFO] Iniciando JAR: $JAR_FILE"

# 7) Rodar em background com nohup (não depender do terminal)
JAVA_OPTS=(
  "-Dserver.port=$PORT"
  "-Dspring.profiles.active=$PROFILE"
  "--add-opens" "java.base/java.lang=ALL-UNNAMED"
  "--add-opens" "java.base/java.util=ALL-UNNAMED"
)

nohup java "${JAVA_OPTS[@]}" -jar "$JAR_FILE" >>"$RUN_LOG" 2>&1 &
APP_PID=$!
echo "[INFO] PID: $APP_PID"

# 8) Aguardar disponibilidade HTTP
ATTEMPTS=40
SLEEP=1
for i in $(seq 1 $ATTEMPTS); do
  sleep "$SLEEP"
  if curl -s -I "http://localhost:$PORT" >/dev/null 2>&1; then
    echo "✅ Aplicação no ar: http://localhost:$PORT"
    exit 0
  fi
  echo "[INFO] Aguardando ($i/$ATTEMPTS)..."
  if grep -q "Exception" "$RUN_LOG"; then
    echo "❌ Erro detectado nos logs. Últimas linhas:"; tail -n 120 "$RUN_LOG"; exit 1
  fi
done

echo "❌ Timeout esperando a aplicação subir. Últimas linhas de log:"
tail -n 160 "$RUN_LOG"
exit 1
