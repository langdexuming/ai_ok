#!/bin/bash
#============================================================
# 边端批量配置工具 - 一键部署脚本
# 用法: ./deploy.sh [目标部署目录]
# 环境变量:
#   DEPLOY_DIR    - 部署目录（默认: /opt/edge）
#   NGINX_CONF    - Nginx配置路径（默认: /etc/nginx/conf.d/edge.conf）
#   EDGE_PORT     - 后端端口（默认: 9090）
#   ARCH          - 目标架构: arm64/amd64（默认: 自动检测）
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/../build"
DEPLOY_DIR=${1:-${DEPLOY_DIR:-"/opt/edge"}}
NGINX_CONF=${NGINX_CONF:-"/etc/nginx/conf.d/edge.conf"}
EDGE_PORT=${EDGE_PORT:-"9090"}

ARCH=${ARCH:-$(uname -m)}
case "${ARCH}" in
    aarch64|arm64) BINARY_SUFFIX="linux-arm64" ;;
    x86_64|amd64)  BINARY_SUFFIX="linux-amd64" ;;
    *)             BINARY_SUFFIX="" ;;
esac

echo "============================================"
echo "  边端批量配置工具 - 一键部署"
echo "============================================"
echo "构建目录:   ${BUILD_DIR}"
echo "部署目录:   ${DEPLOY_DIR}"
echo "目标架构:   ${ARCH}"
echo "后端端口:   ${EDGE_PORT}"
echo "============================================"

BINARY_FILE="${BUILD_DIR}/edge-backend"
if [ -n "${BINARY_SUFFIX}" ] && [ -f "${BUILD_DIR}/edge-backend-${BINARY_SUFFIX}" ]; then
    BINARY_FILE="${BUILD_DIR}/edge-backend-${BINARY_SUFFIX}"
fi

if [ ! -f "${BINARY_FILE}" ]; then
    echo "[错误] 可执行文件不存在: ${BINARY_FILE}"
    echo "[提示] 请先执行 ./build.sh --cross-compile"
    exit 1
fi

if [ ! -d "${BUILD_DIR}/dist" ]; then
    echo "[错误] 前端构建产物不存在，请先执行 ./build.sh"
    exit 1
fi

echo "[步骤1] 创建部署目录..."
sudo mkdir -p "${DEPLOY_DIR}/backend"
sudo mkdir -p "${DEPLOY_DIR}/frontend"
sudo mkdir -p "${DEPLOY_DIR}/logs"
sudo mkdir -p "${DEPLOY_DIR}/upload"

echo "[步骤2] 部署后端..."
sudo cp "${BINARY_FILE}" "${DEPLOY_DIR}/backend/edge-backend"
sudo chmod +x "${DEPLOY_DIR}/backend/edge-backend"

DB_SOURCE="${SCRIPT_DIR}/../edge-backend/i-edge.db"
if [ ! -f "${DEPLOY_DIR}/backend/i-edge.db" ]; then
    if [ -f "${DB_SOURCE}" ]; then
        sudo cp "${DB_SOURCE}" "${DEPLOY_DIR}/backend/i-edge.db"
        echo "[信息] 已复制数据库文件"
    else
        echo "[警告] 数据库文件不存在，请手动放置i-edge.db到 ${DEPLOY_DIR}/backend/"
    fi
fi

echo "[步骤3] 部署前端..."
sudo rm -rf "${DEPLOY_DIR}/frontend/dist"
sudo cp -r "${BUILD_DIR}/dist" "${DEPLOY_DIR}/frontend/dist"

echo "[步骤4] 生成Nginx配置..."
sudo tee "${NGINX_CONF}" > /dev/null << NGINX_EOF
server {
    listen 80;
    server_name localhost;

    root ${DEPLOY_DIR}/frontend/dist;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:${EDGE_PORT}/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_read_timeout 300s;
        proxy_send_timeout 300s;
    }
}
NGINX_EOF

if command -v nginx &> /dev/null; then
    sudo nginx -t && sudo nginx -s reload
    echo "[成功] Nginx配置已生效"
else
    echo "[警告] Nginx未安装，请手动安装并加载配置"
fi

echo "[步骤5] 生成Systemd服务文件..."
sudo tee /etc/systemd/system/edge-backend.service > /dev/null << SERVICE_EOF
[Unit]
Description=Edge Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=${DEPLOY_DIR}/backend
Environment=EDGE_PORT=${EDGE_PORT}
Environment=EDGE_DB_PATH=${DEPLOY_DIR}/backend/i-edge.db
ExecStart=${DEPLOY_DIR}/backend/edge-backend
Restart=always
RestartSec=10
StandardOutput=append:${DEPLOY_DIR}/logs/edge-backend.log
StandardError=append:${DEPLOY_DIR}/logs/edge-backend-error.log

[Install]
WantedBy=multi-user.target
SERVICE_EOF

sudo systemctl daemon-reload
echo "[成功] Systemd服务已注册: edge-backend"

echo ""
echo "============================================"
echo "  部署完成！"
echo "============================================"
echo "启动后端:   sudo systemctl start edge-backend"
echo "停止后端:   sudo systemctl stop edge-backend"
echo "查看状态:   sudo systemctl status edge-backend"
echo "开机自启:   sudo systemctl enable edge-backend"
echo "查看日志:   tail -f ${DEPLOY_DIR}/logs/edge-backend.log"
echo "访问前端:   http://localhost"
echo "============================================"
