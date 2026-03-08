#!/bin/bash
#============================================================
# 边端批量配置工具 - SQLite数据库初始化脚本
# 用法: ./init-db.sh [db_path]
# 说明: 在现有i-edge.db基础上创建协议自动识别所需的新表
#============================================================

set -e

DB_PATH=${1:-"./i-edge.db"}

echo "============================================"
echo "  边端批量配置工具 - 数据库初始化"
echo "============================================"
echo "数据库路径: ${DB_PATH}"
echo "============================================"

if ! command -v sqlite3 &> /dev/null; then
    echo "[错误] sqlite3未安装，请先安装sqlite3"
    exit 1
fi

if [ ! -f "${DB_PATH}" ]; then
    echo "[警告] 数据库文件不存在，将创建新文件: ${DB_PATH}"
fi

echo "[信息] 创建OMC配置表..."
sqlite3 "${DB_PATH}" << 'SQL'
CREATE TABLE IF NOT EXISTS tab_omc_config (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    ip_address      TEXT,
    port            TEXT,
    username        TEXT,
    password        TEXT,
    last_sync_time  TEXT,
    create_time     TEXT,
    update_time     TEXT
);
SQL

echo "[信息] 创建协议识别结果表..."
sqlite3 "${DB_PATH}" << 'SQL'
CREATE TABLE IF NOT EXISTS tab_identify_result (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id      TEXT,
    serial_num      INTEGER,
    device_address  INTEGER,
    device_type     TEXT,
    device_subtype  TEXT,
    library_name    TEXT,
    status          INTEGER,
    fail_reason     TEXT,
    identify_time   TEXT,
    create_time     TEXT
);
CREATE INDEX IF NOT EXISTS idx_identify_result_session ON tab_identify_result(session_id);
SQL

echo "[信息] 创建识别实时数据表..."
sqlite3 "${DB_PATH}" << 'SQL'
CREATE TABLE IF NOT EXISTS tab_identify_realtime_data (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id      TEXT,
    device_type     TEXT,
    device_subtype  TEXT,
    device_code     TEXT,
    channel_name    TEXT,
    channel_code    TEXT,
    channel_type    INTEGER,
    collect_value   TEXT,
    collect_time    TEXT
);
CREATE INDEX IF NOT EXISTS idx_realtime_data_session ON tab_identify_realtime_data(session_id);
SQL

echo ""
echo "[信息] 验证表结构..."
sqlite3 "${DB_PATH}" ".tables" | tr ' ' '\n' | grep -E "(tab_omc_config|tab_identify_result|tab_identify_realtime_data)" | sort

echo ""
echo "[成功] 数据库初始化完成！"
echo "  新增表: tab_omc_config"
echo "  新增表: tab_identify_result"
echo "  新增表: tab_identify_realtime_data"

echo ""
echo "[信息] 现有表统计:"
TABLE_COUNT=$(sqlite3 "${DB_PATH}" "SELECT COUNT(*) FROM sqlite_master WHERE type='table';")
echo "  总计: ${TABLE_COUNT} 张表"
