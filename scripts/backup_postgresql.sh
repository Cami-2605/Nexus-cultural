#!/bin/bash
# ==============================================================================
# Nexus Cultural S.A.S. - Script de Respaldo Automatizado PostgreSQL (RNF-20)
# Frecuencia de ejecución solicitada: Cada 30 de cada mes a las 02:00 AM
# Cronjob correspondiente:
# 0 2 30 * * /home/wilson/uq/Software3/Nexus-cultural/scripts/backup_postgresql.sh >> /home/wilson/uq/Software3/Nexus-cultural/logs/cron_backup.log 2>&1
# ==============================================================================

set -euo pipefail

# 1. Directorios de trabajo
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
BACKUP_DIR="${PROJECT_ROOT}/backups/postgresql"
LOG_FILE="${PROJECT_ROOT}/logs/backup_history.log"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/nexus_db_backup_${TIMESTAMP}.dump.gz"

mkdir -p "$BACKUP_DIR"
mkdir -p "${PROJECT_ROOT}/logs"

echo "======================================================================" >> "$LOG_FILE"
echo "🕒 [$(date '+%Y-%m-%d %H:%M:%S')] Iniciando respaldo mensual de PostgreSQL..." >> "$LOG_FILE"

# 2. Cargar variables de entorno desde .env si existe
if [ -f "${PROJECT_ROOT}/.env" ]; then
    export $(grep -v '^#' "${PROJECT_ROOT}/.env" | xargs -d '\n' 2>/dev/null || true)
fi

# 3. Parámetros de conexión a PostgreSQL
DB_HOST="${PGHOST:-localhost}"
DB_PORT="${PGPORT:-5432}"
DB_NAME="${PGDATABASE:-nexuscultural}"
DB_USER="${PGUSER:-postgres}"

# Si SPRING_DATASOURCE_URL está configurado, extraer datos
if [ -n "${SPRING_DATASOURCE_URL:-}" ]; then
    # Formato jdbc:postgresql://host:port/database
    DB_HOST=$(echo "$SPRING_DATASOURCE_URL" | sed -e 's/.*:\/\///;s/:.*//;s/\/.*//')
    DB_PORT=$(echo "$SPRING_DATASOURCE_URL" | sed -e 's/.*:\/\/[^:]*://;s/\/.*//')
    DB_NAME=$(echo "$SPRING_DATASOURCE_URL" | sed -e 's/.*\///;s/?.*//')
fi

if [ -n "${SPRING_DATASOURCE_USERNAME:-}" ]; then
    DB_USER="$SPRING_DATASOURCE_USERNAME"
fi

if [ -n "${SPRING_DATASOURCE_PASSWORD:-}" ]; then
    export PGPASSWORD="$SPRING_DATASOURCE_PASSWORD"
fi

echo "📦 Parámetros: Host=$DB_HOST, Puerto=$DB_PORT, BD=$DB_NAME, Usuario=$DB_USER" >> "$LOG_FILE"

# 4. Ejecutar pg_dump comprimido
if command -v pg_dump >/dev/null 2>&1; then
    pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -F c -b -v "$DB_NAME" | gzip > "$BACKUP_FILE"
    BACKUP_SIZE=$(du -sh "$BACKUP_FILE" | cut -f1)
    echo "✅ [$(date '+%Y-%m-%d %H:%M:%S')] Respaldo generado con éxito: $BACKUP_FILE ($BACKUP_SIZE)" >> "$LOG_FILE"
else
    echo "⚠️ pg_dump no encontrado en PATH del sistema; simulando o registrando contenedor Docker..." >> "$LOG_FILE"
    # Fallback si PostgreSQL corre en contenedor Docker
    if command -v docker >/dev/null 2>&1 && docker ps | grep -q "postgres"; then
        CONTAINER_ID=$(docker ps --filter "ancestor=postgres" --format "{{.ID}}" | head -n 1)
        docker exec "$CONTAINER_ID" pg_dump -U "$DB_USER" "$DB_NAME" | gzip > "$BACKUP_FILE"
        echo "✅ [$(date '+%Y-%m-%d %H:%M:%S')] Respaldo generado vía contenedor Docker: $BACKUP_FILE" >> "$LOG_FILE"
    else
        echo "❌ [$(date '+%Y-%m-%d %H:%M:%S')] ERROR: Instala postgresql-client o asegúrate de tener pg_dump disponible." >> "$LOG_FILE"
        exit 1
    fi
fi

# 5. Política de retención: Mantener respaldos de los últimos 12 meses (365 días)
find "$BACKUP_DIR" -name "nexus_db_backup_*.dump.gz" -type f -mtime +365 -delete 2>/dev/null || true
echo "🧹 Política de retención ejecutada: eliminadas copias con más de 12 meses." >> "$LOG_FILE"

# 6. Verificación de integridad básica
if [ -s "$BACKUP_FILE" ]; then
    echo "🎉 Respaldo completado satisfactoriamente para RNF-20." >> "$LOG_FILE"
    echo "Respaldo completado en: $BACKUP_FILE"
else
    echo "❌ Archivo de respaldo vacío o corrupto." >> "$LOG_FILE"
    exit 1
fi

