#!/bin/bash
#============================================================
# 云端OMC工具 - 一键部署脚本
# 用法: ./deploy.sh [目标部署目录]
# 说明: 将构建产物部署到指定目录，并配置Nginx
# 环境变量:
#   DEPLOY_DIR    - 部署目录（默认: /opt/omc）
#   NGINX_CONF    - Nginx配置路径（默认: /etc/nginx/conf.d/omc.conf）
#   SERVER_NAME   - 域名（默认: localhost）
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/../build"
DEPLOY_DIR=${1:-${DEPLOY_DIR:-"/opt/omc"}}
NGINX_CONF=${NGINX_CONF:-"/etc/nginx/conf.d/omc.conf"}
SERVER_NAME=${SERVER_NAME:-"localhost"}

echo "============================================"
echo "  云端OMC工具 - 一键部署"
echo "============================================"
echo "构建目录:   ${BUILD_DIR}"
echo "部署目录:   ${DEPLOY_DIR}"
echo "Nginx配置:  ${NGINX_CONF}"
echo "============================================"

if [ ! -f "${BUILD_DIR}/omc-backend-1.0.0.jar" ]; then
    echo "[错误] 构建产物不存在，请先执行 ./build.sh"
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
sudo mkdir -p "/data/omc/upload"

echo "[步骤2] 部署后端..."
sudo cp "${BUILD_DIR}/omc-backend-1.0.0.jar" "${DEPLOY_DIR}/backend/"
if [ -f "${BUILD_DIR}/application.yml.example" ]; then
    if [ ! -f "${DEPLOY_DIR}/backend/application.yml" ]; then
        sudo cp "${BUILD_DIR}/application.yml.example" "${DEPLOY_DIR}/backend/application.yml"
        echo "[提示] 请编辑配置文件: ${DEPLOY_DIR}/backend/application.yml"
    fi
fi

echo "[步骤3] 部署前端..."
sudo rm -rf "${DEPLOY_DIR}/frontend/dist"
sudo cp -r "${BUILD_DIR}/dist" "${DEPLOY_DIR}/frontend/dist"

echo "[步骤4] 生成Nginx配置..."
sudo tee "${NGINX_CONF}" > /dev/null << NGINX_EOF
server {
    listen 80;
    server_name ${SERVER_NAME};

    root ${DEPLOY_DIR}/frontend/dist;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:9000/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_read_timeout 300s;
        proxy_send_timeout 300s;
        client_max_body_size 100m;
    }

    location /doc.html {
        proxy_pass http://127.0.0.1:9000/doc.html;
    }

    location /webjars/ {
        proxy_pass http://127.0.0.1:9000/webjars/;
    }

    location /swagger-resources {
        proxy_pass http://127.0.0.1:9000/swagger-resources;
    }

    location /v2/api-docs {
        proxy_pass http://127.0.0.1:9000/v2/api-docs;
    }
}
NGINX_EOF

echo "[步骤5] 重载Nginx..."
if command -v nginx &> /dev/null; then
    sudo nginx -t && sudo nginx -s reload
    echo "[成功] Nginx配置已生效"
else
    echo "[警告] Nginx未安装，请手动安装并加载配置"
fi

echo "[步骤6] 生成Systemd服务文件..."
sudo tee /etc/systemd/system/omc-backend.service > /dev/null << SERVICE_EOF
[Unit]
Description=OMC Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=${DEPLOY_DIR}/backend
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar ${DEPLOY_DIR}/backend/omc-backend-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=append:${DEPLOY_DIR}/logs/omc-backend.log
StandardError=append:${DEPLOY_DIR}/logs/omc-backend-error.log

[Install]
WantedBy=multi-user.target
SERVICE_EOF

sudo systemctl daemon-reload
echo "[成功] Systemd服务已注册: omc-backend"

echo ""
echo "============================================"
echo "  部署完成！"
echo "============================================"
echo "启动后端:   sudo systemctl start omc-backend"
echo "停止后端:   sudo systemctl stop omc-backend"
echo "查看状态:   sudo systemctl status omc-backend"
echo "开机自启:   sudo systemctl enable omc-backend"
echo "查看日志:   tail -f ${DEPLOY_DIR}/logs/omc-backend.log"
echo "访问前端:   http://${SERVER_NAME}"
echo "API文档:    http://${SERVER_NAME}/doc.html"
echo "============================================"
