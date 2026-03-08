#!/bin/bash
#============================================================
# 云端OMC工具 - 一键构建脚本（前端+后端）
# 用法: ./build.sh [--frontend-only | --backend-only]
#============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/.."
BUILD_DIR="${PROJECT_DIR}/build"

echo "============================================"
echo "  云端OMC工具 - 一键构建"
echo "============================================"

mkdir -p "${BUILD_DIR}"

build_backend() {
    echo ""
    echo "[后端] 开始构建 Java 后端..."
    cd "${PROJECT_DIR}/omc-backend"

    if ! command -v mvn &> /dev/null; then
        echo "[错误] Maven未安装，请先安装Maven"
        exit 1
    fi

    if ! command -v java &> /dev/null; then
        echo "[错误] JDK未安装，请先安装JDK 8+"
        exit 1
    fi

    mvn clean package -DskipTests -q
    cp target/omc-backend-1.0.0.jar "${BUILD_DIR}/"
    cp src/main/resources/application.yml "${BUILD_DIR}/application.yml.example"
    cp src/main/resources/db/init.sql "${BUILD_DIR}/init.sql"
    echo "[后端] 构建完成: ${BUILD_DIR}/omc-backend-1.0.0.jar"
}

build_frontend() {
    echo ""
    echo "[前端] 开始构建 Vue 前端..."
    cd "${PROJECT_DIR}/omc-frontend"

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
    *)
        build_backend
        build_frontend
        ;;
esac

echo ""
echo "============================================"
echo "  构建产物清单（${BUILD_DIR}/）"
echo "============================================"
ls -lh "${BUILD_DIR}/"
echo "============================================"
echo "  构建完成！"
echo "============================================"
