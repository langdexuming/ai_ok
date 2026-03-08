#!/bin/bash
#============================================================
# 边端批量配置工具 - 一键构建脚本（前端+后端）
# 用法: ./build.sh [--frontend-only | --backend-only | --cross-compile]
# 说明: --cross-compile 会同时构建ARM64和AMD64版本
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/.."
BUILD_DIR="${PROJECT_DIR}/build"

echo "============================================"
echo "  边端批量配置工具 - 一键构建"
echo "============================================"

mkdir -p "${BUILD_DIR}"

build_backend() {
    echo ""
    echo "[后端] 开始构建 Go 后端..."
    cd "${PROJECT_DIR}/edge-backend"

    if ! command -v go &> /dev/null; then
        echo "[错误] Go未安装，请先安装Go 1.21+"
        exit 1
    fi

    echo "[后端] 下载依赖..."
    go mod tidy

    echo "[后端] 编译当前平台..."
    go build -o "${BUILD_DIR}/edge-backend" .
    echo "[后端] 当前平台构建完成: ${BUILD_DIR}/edge-backend"

    if [ "$1" = "cross" ]; then
        echo "[后端] 交叉编译 Linux ARM64..."
        GOOS=linux GOARCH=arm64 go build -o "${BUILD_DIR}/edge-backend-linux-arm64" .
        echo "[后端] ARM64构建完成: ${BUILD_DIR}/edge-backend-linux-arm64"

        echo "[后端] 交叉编译 Linux AMD64..."
        GOOS=linux GOARCH=amd64 go build -o "${BUILD_DIR}/edge-backend-linux-amd64" .
        echo "[后端] AMD64构建完成: ${BUILD_DIR}/edge-backend-linux-amd64"
    fi
}

build_frontend() {
    echo ""
    echo "[前端] 开始构建 Vue 前端..."
    cd "${PROJECT_DIR}/edge-frontend"

    if ! command -v node &> /dev/null; then
        echo "[错误] Node.js未安装，请先安装Node.js 16+"
        exit 1
    fi

    if [ ! -d "node_modules" ]; then
        echo "[前端] 安装依赖..."
        npm install --silent
    fi

    npx vite build
    rm -rf "${BUILD_DIR}/dist"
    cp -r dist "${BUILD_DIR}/dist"
    echo "[前端] 构建完成: ${BUILD_DIR}/dist/"
}

case "${1}" in
    --frontend-only)
        build_frontend
        ;;
    --backend-only)
        build_backend
        ;;
    --cross-compile)
        build_backend "cross"
        build_frontend
        ;;
    *)
        build_backend
        build_frontend
        ;;
esac

echo ""
echo "============================================"
echo "  构建产物清单（${BUILD_DIR}/）"
echo "============================================"
ls -lh "${BUILD_DIR}/" 2>/dev/null
echo "============================================"
echo "  构建完成！"
echo "============================================"
