#!/bin/bash
#============================================================
# 边端批量配置工具 - SQLite数据库备份脚本
# 用法: ./backup-db.sh [db_path] [备份目录]
#============================================================

set -e

DB_PATH=${1:-"./i-edge.db"}
BACKUP_DIR=${2:-"./backup"}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/i-edge_${TIMESTAMP}.db"

echo "============================================"
echo "  边端批量配置工具 - 数据库备份"
echo "============================================"

if [ ! -f "${DB_PATH}" ]; then
    echo "[错误] 数据库文件不存在: ${DB_PATH}"
    exit 1
fi

mkdir -p "${BACKUP_DIR}"

echo "[信息] 开始备份数据库..."
if command -v sqlite3 &> /dev/null; then
    sqlite3 "${DB_PATH}" ".backup '${BACKUP_FILE}'"
else
    cp "${DB_PATH}" "${BACKUP_FILE}"
fi

if [ $? -eq 0 ]; then
    FILESIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
    echo "[成功] 备份完成！"
    echo "  备份文件: ${BACKUP_FILE}"
    echo "  文件大小: ${FILESIZE}"

    KEEP_DAYS=30
    echo "[信息] 清理${KEEP_DAYS}天前的备份..."
    find "${BACKUP_DIR}" -name "i-edge_*.db" -mtime +${KEEP_DAYS} -delete
else
    echo "[失败] 备份失败"
    exit 1
fi
