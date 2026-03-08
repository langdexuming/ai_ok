#!/bin/bash
#============================================================
# 边端批量配置工具 - 后端启动脚本
# 用法: ./start.sh [--foreground]
# 环境变量:
#   EDGE_PORT     - 服务端口（默认: 9090）
#   EDGE_DB_PATH  - SQLite数据库路径（默认: ./i-edge.db）
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="${SCRIPT_DIR}/.."
BINARY="${APP_HOME}/build/edge-backend"
LOG_DIR="${APP_HOME}/logs"
PID_FILE="${APP_HOME}/edge-backend.pid"

export EDGE_PORT=${EDGE_PORT:-"9090"}
export EDGE_DB_PATH=${EDGE_DB_PATH:-"${APP_HOME}/i-edge.db"}

if [ ! -f "${BINARY}" ]; then
    echo "[错误] 可执行文件不存在: ${BINARY}"
    echo "[提示] 请先执行 ./build.sh 构建项目"
    exit 1
fi

if [ ! -f "${EDGE_DB_PATH}" ]; then
    echo "[警告] 数据库文件不存在: ${EDGE_DB_PATH}"
    echo "[提示] 请将i-edge.db放到指定路径，或执行 ./init-db.sh 初始化"
fi

if [ -f "${PID_FILE}" ]; then
    OLD_PID=$(cat "${PID_FILE}")
    if kill -0 "${OLD_PID}" 2>/dev/null; then
        echo "[警告] 服务已在运行中（PID: ${OLD_PID}）"
        echo "[提示] 请先执行 ./stop.sh 停止服务"
        exit 1
    fi
    rm -f "${PID_FILE}"
fi

mkdir -p "${LOG_DIR}"

echo "============================================"
echo "  边端批量配置工具 - 启动后端服务"
echo "============================================"
echo "可执行文件: ${BINARY}"
echo "服务端口:   ${EDGE_PORT}"
echo "数据库路径: ${EDGE_DB_PATH}"
echo "日志目录:   ${LOG_DIR}"
echo "============================================"

if [ "$1" = "--foreground" ]; then
    echo "[信息] 前台启动..."
    "${BINARY}"
else
    echo "[信息] 后台启动..."
    nohup "${BINARY}" > "${LOG_DIR}/edge-backend.log" 2>&1 &
    echo $! > "${PID_FILE}"
    sleep 2

    if kill -0 "$(cat ${PID_FILE})" 2>/dev/null; then
        echo "[成功] 服务启动成功（PID: $(cat ${PID_FILE})）"
        echo "[信息] 日志文件: ${LOG_DIR}/edge-backend.log"
        echo "[信息] 服务地址: http://localhost:${EDGE_PORT}"
    else
        echo "[失败] 服务启动失败，请查看日志:"
        tail -20 "${LOG_DIR}/edge-backend.log"
        exit 1
    fi
fi
