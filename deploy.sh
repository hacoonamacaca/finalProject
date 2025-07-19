#!/bin/bash

# GoldenBowl Docker 部署腳本

echo "🚀 開始部署 GoldenBowl 專案..."

# 檢查 Docker 是否安裝
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安裝，請先安裝 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安裝，請先安裝 Docker Compose"
    exit 1
fi

# 停止現有容器
echo "🛑 停止現有容器..."
docker-compose down

# 清理舊的映像檔
echo "🧹 清理舊的映像檔..."
docker system prune -f

# 建立 SSL 目錄 (如果不存在)
mkdir -p ssl

# 建置並啟動服務
echo "🔨 建置並啟動服務..."
docker-compose up --build -d

# 等待服務啟動
echo "⏳ 等待服務啟動..."
sleep 30

# 檢查服務狀態
echo "📊 檢查服務狀態..."
docker-compose ps

# 檢查資料庫連接
echo "🔍 檢查資料庫連接..."
docker-compose exec db /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P GoldenBowl123! -Q "SELECT 'Database is running' as status"

# 顯示訪問資訊
echo ""
echo "✅ 部署完成！"
echo ""
echo "🌐 訪問地址："
echo "   客戶端前端: http://localhost:3000"
echo "   店家前端:   http://localhost:3001"
echo "   管理後台:   http://localhost:3002"
echo "   後端 API:   http://localhost:8080"
echo "   資料庫:     localhost:1433"
echo ""
echo "📝 常用命令："
echo "   查看日誌:   docker-compose logs -f [service-name]"
echo "   停止服務:   docker-compose down"
echo "   重啟服務:   docker-compose restart [service-name]"
echo "   進入容器:   docker-compose exec [service-name] bash"
echo "" 