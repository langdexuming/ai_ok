#!/bin/bash
#============================================================
# 云端OMC工具 - MySQL数据库初始化脚本
# 用法: ./init-db.sh [mysql_host] [mysql_port] [mysql_user] [mysql_password]
#============================================================

set -e

MYSQL_HOST=${1:-"localhost"}
MYSQL_PORT=${2:-"3306"}
MYSQL_USER=${3:-"root"}
MYSQL_PASSWORD=${4:-"root"}

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SQL_FILE="${SCRIPT_DIR}/../omc-backend/src/main/resources/db/init.sql"

echo "============================================"
echo "  云端OMC工具 - 数据库初始化"
echo "============================================"
echo "MySQL地址: ${MYSQL_HOST}:${MYSQL_PORT}"
echo "MySQL用户: ${MYSQL_USER}"
echo "SQL脚本:   ${SQL_FILE}"
echo "============================================"

if [ ! -f "${SQL_FILE}" ]; then
    echo "[错误] SQL脚本文件不存在: ${SQL_FILE}"
    exit 1
fi

if ! command -v mysql &> /dev/null; then
    echo "[错误] mysql客户端未安装，请先安装MySQL客户端"
    exit 1
fi

echo "[信息] 开始执行数据库初始化脚本..."
mysql -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" < "${SQL_FILE}"

if [ $? -eq 0 ]; then
    echo "[成功] 数据库初始化完成！"
    echo "  数据库名: pai"
    echo "  已创建表: omc_dynamic_library, omc_dynamic_library_file,"
    echo "           omc_total_frequency, omc_serial_frequency,"
    echo "           omc_verify_config, omc_device, omc_device_config,"
    echo "           omc_device_type, omc_device_subtype"
else
    echo "[失败] 数据库初始化失败，请检查MySQL连接配置"
    exit 1
fi
