#!/bin/bash
#============================================================
# 云端OMC工具 - 后端启动脚本
# 用法: ./start.sh [--foreground]
# 环境变量:
#   JAVA_OPTS    - JVM参数（默认: -Xms512m -Xmx1024m）
#   APP_PORT     - 服务端口（默认: 9000）
#   DB_HOST      - 数据库地址（默认: localhost）
#   DB_PORT      - 数据库端口（默认: 3306）
#   DB_USER      - 数据库用户（默认: root）
#   DB_PASSWORD  - 数据库密码（默认: root）
#   UPLOAD_PATH  - 文件上传路径（默认: /data/omc/upload）
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_HOME="${SCRIPT_DIR}/.."
JAR_FILE="${APP_HOME}/build/omc-backend-1.0.0.jar"
LOG_DIR="${APP_HOME}/logs"
PID_FILE="${APP_HOME}/omc-backend.pid"

JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx1024m"}
APP_PORT=${APP_PORT:-"9000"}
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_USER=${DB_USER:-"root"}
DB_PASSWORD=${DB_PASSWORD:-"root"}
UPLOAD_PATH=${UPLOAD_PATH:-"/data/omc/upload"}

if [ ! -f "${JAR_FILE}" ]; then
    echo "[错误] JAR包不存在: ${JAR_FILE}"
    echo "[提示] 请先执行 ./build.sh 构建项目"
    exit 1
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
mkdir -p "${UPLOAD_PATH}"

SPRING_OPTS="--server.port=${APP_PORT}"
SPRING_OPTS="${SPRING_OPTS} --spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/pai?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
SPRING_OPTS="${SPRING_OPTS} --spring.datasource.username=${DB_USER}"
SPRING_OPTS="${SPRING_OPTS} --spring.datasource.password=${DB_PASSWORD}"
SPRING_OPTS="${SPRING_OPTS} --omc.upload.path=${UPLOAD_PATH}"

echo "============================================"
echo "  云端OMC工具 - 启动后端服务"
echo "============================================"
echo "JAR文件:    ${JAR_FILE}"
echo "服务端口:   ${APP_PORT}"
echo "数据库:     ${DB_HOST}:${DB_PORT}/pai"
echo "上传路径:   ${UPLOAD_PATH}"
echo "日志目录:   ${LOG_DIR}"
echo "============================================"

if [ "$1" = "--foreground" ]; then
    echo "[信息] 前台启动..."
    java ${JAVA_OPTS} -jar "${JAR_FILE}" ${SPRING_OPTS}
else
    echo "[信息] 后台启动..."
    nohup java ${JAVA_OPTS} -jar "${JAR_FILE}" ${SPRING_OPTS} \
        > "${LOG_DIR}/omc-backend.log" 2>&1 &
    echo $! > "${PID_FILE}"
    sleep 2

    if kill -0 "$(cat ${PID_FILE})" 2>/dev/null; then
        echo "[成功] 服务启动成功（PID: $(cat ${PID_FILE})）"
        echo "[信息] 日志文件: ${LOG_DIR}/omc-backend.log"
        echo "[信息] API文档:  http://localhost:${APP_PORT}/doc.html"
    else
        echo "[失败] 服务启动失败，请查看日志:"
        tail -20 "${LOG_DIR}/omc-backend.log"
        exit 1
    fi
fi
