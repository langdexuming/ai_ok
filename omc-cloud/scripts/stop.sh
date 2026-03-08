#!/bin/bash
#============================================================
# 云端OMC工具 - 后端停止脚本
# 用法: ./stop.sh [--force]
#============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="${SCRIPT_DIR}/.."
PID_FILE="${APP_HOME}/omc-backend.pid"

echo "============================================"
echo "  云端OMC工具 - 停止后端服务"
echo "============================================"

if [ ! -f "${PID_FILE}" ]; then
    echo "[信息] PID文件不存在，尝试通过进程名查找..."
    PID=$(pgrep -f "omc-backend-1.0.0.jar" 2>/dev/null || true)
    if [ -z "${PID}" ]; then
        echo "[信息] 服务未运行"
        exit 0
    fi
else
    PID=$(cat "${PID_FILE}")
fi

if ! kill -0 "${PID}" 2>/dev/null; then
    echo "[信息] 进程（PID: ${PID}）已不存在"
    rm -f "${PID_FILE}"
    exit 0
fi

echo "[信息] 正在停止服务（PID: ${PID}）..."

if [ "$1" = "--force" ]; then
    kill -9 "${PID}" 2>/dev/null
    echo "[信息] 已强制终止进程"
else
    kill "${PID}" 2>/dev/null
    WAIT=0
    while kill -0 "${PID}" 2>/dev/null; do
        if [ ${WAIT} -ge 30 ]; then
            echo "[警告] 等待超时，强制终止..."
            kill -9 "${PID}" 2>/dev/null
            break
        fi
        sleep 1
        WAIT=$((WAIT + 1))
    done
fi

rm -f "${PID_FILE}"
echo "[成功] 服务已停止"
