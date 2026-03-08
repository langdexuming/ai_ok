#!/bin/bash
#============================================================
# 云端OMC工具 - MySQL数据库备份脚本
# 用法: ./backup-db.sh [备份目录]
# 环境变量:
#   DB_HOST      - 数据库地址（默认: localhost）
#   DB_PORT      - 数据库端口（默认: 3306）
#   DB_USER      - 数据库用户（默认: root）
#   DB_PASSWORD  - 数据库密码（默认: root）
#============================================================

set -e

BACKUP_DIR=${1:-"/data/omc/backup"}
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_USER=${DB_USER:-"root"}
DB_PASSWORD=${DB_PASSWORD:-"root"}
DB_NAME="pai"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/pai_${TIMESTAMP}.sql"

echo "============================================"
echo "  云端OMC工具 - 数据库备份"
echo "============================================"

mkdir -p "${BACKUP_DIR}"

if ! command -v mysqldump &> /dev/null; then
    echo "[错误] mysqldump未安装"
    exit 1
fi

echo "[信息] 开始备份数据库 ${DB_NAME}..."
mysqldump -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASSWORD}" \
    --single-transaction --routines --triggers \
    "${DB_NAME}" > "${BACKUP_FILE}"

if [ $? -eq 0 ]; then
    FILESIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
    echo "[成功] 备份完成！"
    echo "  备份文件: ${BACKUP_FILE}"
    echo "  文件大小: ${FILESIZE}"

    KEEP_DAYS=30
    echo "[信息] 清理${KEEP_DAYS}天前的备份..."
    find "${BACKUP_DIR}" -name "pai_*.sql" -mtime +${KEEP_DAYS} -delete
else
    echo "[失败] 备份失败"
    exit 1
fi
