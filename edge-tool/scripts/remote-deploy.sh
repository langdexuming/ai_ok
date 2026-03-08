#!/bin/bash
#============================================================
# 边端批量配置工具 - 远程部署脚本（部署到边缘网关）
# 用法: ./remote-deploy.sh <target_ip> [target_user] [target_dir]
# 说明: 通过SSH将构建产物部署到远程边缘网关设备
#============================================================

set -e

TARGET_IP=${1}
TARGET_USER=${2:-"root"}
TARGET_DIR=${3:-"/opt/edge"}
EDGE_PORT=${EDGE_PORT:-"9090"}

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/../build"

if [ -z "${TARGET_IP}" ]; then
    echo "用法: $0 <target_ip> [target_user] [target_dir]"
    echo "示例: $0 192.168.1.100 root /opt/edge"
    exit 1
fi

echo "============================================"
echo "  边端批量配置工具 - 远程部署"
echo "============================================"
echo "目标设备:   ${TARGET_USER}@${TARGET_IP}"
echo "部署目录:   ${TARGET_DIR}"
echo "后端端口:   ${EDGE_PORT}"
echo "============================================"

REMOTE_ARCH=$(ssh ${TARGET_USER}@${TARGET_IP} "uname -m" 2>/dev/null)
echo "[信息] 目标设备架构: ${REMOTE_ARCH}"

case "${REMOTE_ARCH}" in
    aarch64|arm64) BINARY="edge-backend-linux-arm64" ;;
    x86_64)        BINARY="edge-backend-linux-amd64" ;;
    *)             BINARY="edge-backend" ;;
esac

if [ ! -f "${BUILD_DIR}/${BINARY}" ]; then
    if [ -f "${BUILD_DIR}/edge-backend" ]; then
        BINARY="edge-backend"
    else
        echo "[错误] 未找到匹配架构的可执行文件"
        echo "[提示] 请先执行 ./build.sh --cross-compile"
        exit 1
    fi
fi

echo "[步骤1] 创建远程目录..."
ssh ${TARGET_USER}@${TARGET_IP} "mkdir -p ${TARGET_DIR}/backend ${TARGET_DIR}/frontend ${TARGET_DIR}/logs"

echo "[步骤2] 停止现有服务..."
ssh ${TARGET_USER}@${TARGET_IP} "systemctl stop edge-backend 2>/dev/null || true; pkill -f edge-backend 2>/dev/null || true"

echo "[步骤3] 传输后端可执行文件..."
scp "${BUILD_DIR}/${BINARY}" ${TARGET_USER}@${TARGET_IP}:${TARGET_DIR}/backend/edge-backend
ssh ${TARGET_USER}@${TARGET_IP} "chmod +x ${TARGET_DIR}/backend/edge-backend"

echo "[步骤4] 传输前端文件..."
scp -r "${BUILD_DIR}/dist" ${TARGET_USER}@${TARGET_IP}:${TARGET_DIR}/frontend/

DB_SOURCE="${SCRIPT_DIR}/../edge-backend/i-edge.db"
if [ -f "${DB_SOURCE}" ]; then
    DB_EXISTS=$(ssh ${TARGET_USER}@${TARGET_IP} "test -f ${TARGET_DIR}/backend/i-edge.db && echo yes || echo no")
    if [ "${DB_EXISTS}" = "no" ]; then
        echo "[步骤5] 传输数据库文件..."
        scp "${DB_SOURCE}" ${TARGET_USER}@${TARGET_IP}:${TARGET_DIR}/backend/i-edge.db
    else
        echo "[步骤5] 数据库文件已存在，跳过传输"
    fi
fi

echo "[步骤6] 创建Systemd服务并启动..."
ssh ${TARGET_USER}@${TARGET_IP} << REMOTE_EOF
cat > /etc/systemd/system/edge-backend.service << 'SERVICE_EOF'
[Unit]
Description=Edge Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=${TARGET_DIR}/backend
Environment=EDGE_PORT=${EDGE_PORT}
Environment=EDGE_DB_PATH=${TARGET_DIR}/backend/i-edge.db
ExecStart=${TARGET_DIR}/backend/edge-backend
Restart=always
RestartSec=10
StandardOutput=append:${TARGET_DIR}/logs/edge-backend.log
StandardError=append:${TARGET_DIR}/logs/edge-backend-error.log

[Install]
WantedBy=multi-user.target
SERVICE_EOF

systemctl daemon-reload
systemctl enable edge-backend
systemctl start edge-backend
sleep 2
systemctl status edge-backend --no-pager
REMOTE_EOF

echo ""
echo "============================================"
echo "  远程部署完成！"
echo "============================================"
echo "目标设备: ${TARGET_USER}@${TARGET_IP}"
echo "服务状态: ssh ${TARGET_USER}@${TARGET_IP} systemctl status edge-backend"
echo "查看日志: ssh ${TARGET_USER}@${TARGET_IP} tail -f ${TARGET_DIR}/logs/edge-backend.log"
echo "访问前端: http://${TARGET_IP}"
echo "============================================"
